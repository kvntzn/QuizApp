package com.heathkev.quizado.utils

import android.view.View
import android.view.WindowInsets

object NoopWindowInsetsListener : View.OnApplyWindowInsetsListener {
    override fun onApplyWindowInsets(v: View, insets: WindowInsets): WindowInsets {
        return insets
    }
}

object HeightTopWindowInsetsListener : View.OnApplyWindowInsetsListener {
    override fun onApplyWindowInsets(v: View, insets: WindowInsets): WindowInsets {
        val topInset = insets.systemWindowInsetTop
        if (v.layoutParams.height != topInset) {
            v.layoutParams.height = topInset
            v.requestLayout()
        }
        return insets
    }
}
