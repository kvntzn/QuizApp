package com.heathkev.quizado.utils

import android.graphics.drawable.Drawable
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

fun Toolbar.asGlideTarget(): Target<Drawable> = object : CustomTarget<Drawable>() {

    override fun onLoadCleared(placeholder: Drawable?) {
        navigationIcon = placeholder
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        navigationIcon = resource
    }
}
