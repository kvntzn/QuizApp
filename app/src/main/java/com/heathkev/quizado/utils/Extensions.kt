package com.heathkev.quizado.utils

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.BuildCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.ViewDataBinding
import androidx.drawerlayout.widget.DrawerLayout
import com.heathkev.quizado.model.Theme

/**
 * Helper to force a when statement to assert all options are matched in a when statement.
 *
 * By default, Kotlin doesn't care if all branches are handled in a when statement. However, if you
 * use the when statement as an expression (with a value) it will force all cases to be handled.
 *
 * This helper is to make a lightweight way to say you meant to match all of them.
 *
 * Usage:
 *
 * ```
 * when(sealedObject) {
 *     is OneType -> //
 *     is AnotherType -> //
 * }.checkAllMatched
 */
val <T> T.checkAllMatched: T
    get() = this


inline fun <T : ViewDataBinding> T.executeAfter(block: T.() -> Unit) {
    block()
    executePendingBindings()
}

private fun createStateForView(view: View) = ViewPaddingState(view.paddingLeft,
    view.paddingTop, view.paddingRight, view.paddingBottom, view.paddingStart, view.paddingEnd)

data class ViewPaddingState(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
    val start: Int,
    val end: Int
)

fun View.doOnApplyWindowInsets(f: (View, WindowInsetsCompat, ViewPaddingState) -> Unit) {
    // Create a snapshot of the view's padding state
    val paddingState = createStateForView(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        f(v, insets, paddingState)
        insets
    }
    requestApplyInsetsWhenAttached()
}

/**
 * Call [View.requestApplyInsets] in a safe away. If we're attached it calls it straight-away.
 * If not it sets an [View.OnAttachStateChangeListener] and waits to be attached before calling
 * [View.requestApplyInsets].
 */
fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

@SuppressLint("NewApi") // Lint does not understand isAtLeastQ currently
fun DrawerLayout.shouldCloseDrawerFromBackPress(): Boolean {
    if (BuildCompat.isAtLeastR()) {
        // If we're running on Q, and this call to closeDrawers is from a key event
        // (for back handling), we should only honor it IF the device is not currently
        // in gesture mode. We approximate that by checking the system gesture insets
        return rootWindowInsets?.let {
            val systemGestureInsets = it.systemGestureInsets
            return systemGestureInsets.left == 0 && systemGestureInsets.right == 0
        } ?: false
    }
    // On P and earlier, always close the drawer
    return true
}

fun AppCompatActivity.updateForTheme(theme: Theme) = when (theme) {
    Theme.DARK -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
    Theme.LIGHT -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
    Theme.SYSTEM -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    Theme.BATTERY_SAVER -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
}

