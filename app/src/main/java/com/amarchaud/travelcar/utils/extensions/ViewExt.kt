package com.amarchaud.travelcar.utils.extensions

import android.app.Activity
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.annotation.AnimRes
import androidx.appcompat.app.AppCompatActivity.OVERRIDE_TRANSITION_OPEN
import com.amarchaud.travelcar.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import java.util.*

fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow {
        val listener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                trySend(s)
            }
        }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}

@Suppress("DEPRECATION")
fun Activity.overrideActivityTransitionCompat(
    @AnimRes enterAnim: Int,
    @AnimRes exitAnim: Int
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, enterAnim, exitAnim)
    } else {
        overridePendingTransition(enterAnim, exitAnim)
    }
}
