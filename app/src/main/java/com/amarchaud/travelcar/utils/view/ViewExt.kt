package com.amarchaud.travelcar.utils

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart


fun View.setTopBottomInsets(addInsets: (topInsets: Int, bottomInsets: Int) -> Unit) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        addInsets(
            insets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
            insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            //insets.systemWindowInsetTop,
            //insets.systemWindowInsetBottom
        )
        insets
    }
}

fun View.setBottomPadding(bottom: Int) {
    this.setPadding(
        this.paddingLeft,
        this.paddingTop,
        this.paddingRight,
        bottom
    )
}

fun Window.setLightBars() {
    setLightStatusBar()
    setLightNavigationBar()
}

fun Window.setDarkBars() {
    setDarkStatusBar()
    setDarkNavigationBar()
}

fun Window.setLightStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.setSystemBarsAppearance(
            APPEARANCE_LIGHT_STATUS_BARS,
            APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        val flags = decorView.systemUiVisibility
        decorView.systemUiVisibility = (flags or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    }
}

fun Window.setLightNavigationBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

        insetsController?.setSystemBarsAppearance(
            APPEARANCE_LIGHT_NAVIGATION_BARS,
            APPEARANCE_LIGHT_NAVIGATION_BARS
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val flags = decorView.systemUiVisibility
        decorView.systemUiVisibility = (flags or
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
    }
}

fun Window.setDarkStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
    } else {
        val flags = decorView.systemUiVisibility
        decorView.systemUiVisibility = (flags and
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv())
    }
}

fun Window.setDarkNavigationBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_NAVIGATION_BARS)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val flags = decorView.systemUiVisibility
        decorView.systemUiVisibility = (flags and
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv())
    }
}

fun View.setMargins(l: Int, t: Int, r: Int, b: Int) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(l, t, r, b)
        requestLayout()
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
fun AppCompatEditText.textChanges(): Flow<CharSequence?> {
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

@OptIn(ExperimentalCoroutinesApi::class)
fun SearchView.textChanges(): Flow<CharSequence?> {
    return callbackFlow {
        val listener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                trySend(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                trySend(newText)
                return true
            }
        }
        setOnQueryTextListener(listener)
        awaitClose { setOnQueryTextListener(null) }
    }
}

fun List<String>.toReadableString() = this.toString().removePrefix("[").removeSuffix("]")
