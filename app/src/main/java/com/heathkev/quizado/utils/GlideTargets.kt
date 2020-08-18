package com.heathkev.quizado.utils

import android.graphics.drawable.Drawable
import android.view.MenuItem
import com.bumptech.glide.request.target.BaseTarget
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

fun MenuItem.asGlideTarget(size: Int): Target<Drawable> = object : BaseTarget<Drawable>() {

    override fun getSize(cb: SizeReadyCallback) {
        cb.onSizeReady(size, size)
    }

    override fun removeCallback(cb: SizeReadyCallback) {}

    override fun onLoadStarted(placeholder: Drawable?) {
        icon = placeholder
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        icon = errorDrawable
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        icon = placeholder
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        icon = resource
    }
}
