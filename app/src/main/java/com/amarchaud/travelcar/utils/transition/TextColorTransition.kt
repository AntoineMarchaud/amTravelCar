package com.amarchaud.travelcar.utils.transition

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import com.amarchaud.travelcar.R


class TextColorTransition(context: Context, attr: AttributeSet? = null) :
    Transition(context, attr) {


    private val PROPNAME_TEXT_COLOR = "com.amarchaud.travelcar:TextColorTransition:textColor"
    private val TRANSITION_PROPERTIES = arrayOf(PROPNAME_TEXT_COLOR)

    fun addExtraProperties(view: TextView, extra: Bundle) {
        extra.putInt(PROPNAME_TEXT_COLOR, view.currentTextColor)
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

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null) {
            return null
        }
        val startTextColor = startValues.values[PROPNAME_TEXT_COLOR] as Int?
        val endTextColor = endValues.values[PROPNAME_TEXT_COLOR] as Int?
        val textView = endValues.view as TextView

        val argbEvaluator = ArgbEvaluator()
        val animator = ValueAnimator.ofFloat(0f, 1.0f)
        animator.addUpdateListener { animation: ValueAnimator ->
            val color = argbEvaluator.evaluate(
                animation.animatedFraction,
                startTextColor,
                endTextColor
            ) as Int
            textView.setTextColor(color)
        }
        return animator
    }

    private fun captureValues(transitionValues: TransitionValues) {
        if (transitionValues.view is TextView) {
            val extraData =
                transitionValues.view.getTag(R.id.tag_transition_extra_properties) as? Bundle

            val color = if (extraData?.containsKey(PROPNAME_TEXT_COLOR) == true) {
                extraData.getInt(PROPNAME_TEXT_COLOR)
            } else {
                (transitionValues.view as TextView).currentTextColor
            }

            Log.d("TextColorTransition", "captureValues : $color")
            transitionValues.values[PROPNAME_TEXT_COLOR] = color
        }
    }
}


