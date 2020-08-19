package com.heathkev.quizado.ui.signin

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseUser
import com.heathkev.quizado.R
import com.heathkev.quizado.ui.MainActivityViewModel
import com.heathkev.quizado.utils.asGlideTarget
import java.util.zip.Inflater

fun Toolbar.setupProfileMenuItem(
    menu: Menu,
    inflater: MenuInflater,
    viewModel: MainActivityViewModel,
    lifecycleOwner: LifecycleOwner
) {
    inflater.inflate(R.menu.profile_menu, menu)

    val profileItem = menu.findItem(R.id.action_profile) ?: return
    profileItem.setOnMenuItemClickListener {
        Toast.makeText(context,"Test",Toast.LENGTH_SHORT).show()
        true
    }

    setProfileContentDescription(profileItem, resources)

    val avatarSize = resources.getDimensionPixelSize(R.dimen.nav_account_image_size)
    val target = profileItem.asGlideTarget(avatarSize)
    viewModel.currentUser.observe(lifecycleOwner, Observer {
        setProfileAvatar(context, target, it)
    })
}

fun setProfileContentDescription(item: MenuItem, res: Resources) {
    val description = res.getString(R.string.quiz)
    MenuItemCompat.setContentDescription(item, description)
}

fun setProfileAvatar(
    context: Context,
    target: Target<Drawable>,
    user: FirebaseUser?,
    placeholder: Int = R.drawable.ic_default_profile_avatar
) {
    // Inflate the drawable for proper tinting
    val placeholderDrawable = AppCompatResources.getDrawable(context, placeholder)
    if(user!= null){
        when (user.photoUrl) {
            null -> {
                Glide.with(context)
                    .load(placeholderDrawable)
                    .apply(RequestOptions.circleCropTransform())
                    .into(target)
            }
            else -> {
                Glide.with(context)
                    .load(user.photoUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(target)
            }
        }
    }
}