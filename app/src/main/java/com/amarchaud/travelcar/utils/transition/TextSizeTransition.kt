package com.amarchaud.travelcar.utils.transition

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.util.Log
import android.util.Property
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import com.amarchaud.travelcar.R


class TextSizeTransition(context: Context, attr: AttributeSet? = null) : Transition(context, attr) {

    private val PROPNAME_TEXT_SIZE = "com.amarchaud.travelcar:TextSizeTransition:textsize"
    private val TRANSITION_PROPERTIES = arrayOf(PROPNAME_TEXT_SIZE)

    companion object {

        private val TEXT_SIZE_PROPERTY: Property<TextView, Float> =
            object : Property<TextView, Float>(Float::class.java, "textSize") {

                override operator fun get(textView: TextView): Float {
                    return textView.textSize
                }

                override operator fun set(textView: TextView, textSizePixels: Float?) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePixels!!)
                }
            }
    }

    fun addExtraProperties(view: TextView, extra: Bundle) {
        extra.putFloat(PROPNAME_TEXT_SIZE, view.textSize)
    }

    override fun getTransitionProperties(): Array<String> {
        return TRANSITION_PROPERTIES
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues) {
        if (transitionValues.view is TextView) {

            val extraData =
                transitionValues.view.getTag(R.id.tag_transition_extra_properties) as? Bundle

            val textSize = if (extraData?.containsKey(PROPNAME_TEXT_SIZE) == true) {
                extraData.getFloat(PROPNAME_TEXT_SIZE)
            } else {
                (transitionValues.view as TextView).textSize
            }

            Log.d("TextSizeTransition", "captureValues : $textSize")
            transitionValues.values[PROPNAME_TEXT_SIZE] = textSize
        }
    }

    override fun createAnimator(
        sceneRoot: ViewGroup?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null) {
            return null
        }
        val startSize = startValues.values[PROPNAME_TEXT_SIZE] as Float
        val endSize = endValues.values[PROPNAME_TEXT_SIZE] as Float
        if (startSize == endSize) {
            return null
        }
        val view = endValues.view as TextView
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, startSize)
        return ObjectAnimator.ofFloat(view, TEXT_SIZE_PROPERTY, startSize, endSize)
    }
}