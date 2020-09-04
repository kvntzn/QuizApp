package com.heathkev.quizado.ui

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.heathkev.quizado.R
import com.heathkev.quizado.model.QuizListModel
import com.heathkev.quizado.ui.list.ListFragment.Companion.Level.*
import com.heathkev.quizado.ui.list.QuizListAdapter
import com.heathkev.quizado.utils.CircularOutlineProvider
import timber.log.Timber
import java.util.*

@BindingAdapter("clipToCircle")
fun clipToCircle(view: View, clip: Boolean) {
    view.clipToOutline = clip
    view.outlineProvider = if (clip) CircularOutlineProvider else null
}

@BindingAdapter("goneUnless")
fun goneUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("invisibleUnless")
fun invisibleUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<QuizListModel>?) {
    val adapter = recyclerView.adapter as QuizListAdapter
    adapter.submitList(data)
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    val image = if (imgUrl != null) imgUrl.toUri().buildUpon().scheme("https")
        .build() else R.drawable.ic_unknown
    Glide.with(imgView.context)
        .load(image)
        .apply(
            RequestOptions()
                .placeholder(R.drawable.placeholder_image)
        )
        .into(imgView)
}

@BindingAdapter("imageUrl")
fun bindImageUri(imgView: ImageView, imgUrl: Uri?) {
    val image = if (imgUrl != null && imgUrl != Uri.EMPTY) {
        imgUrl.buildUpon().scheme("https")
            ?.build()
    } else{
        R.drawable.ic_unknown
    }

    Glide.with(imgView.context)
        .load(image)
        .apply(
            RequestOptions()
                .placeholder(R.drawable.placeholder_image)
        )
        .into(imgView)
}

@BindingAdapter(value = ["defaultImageUri", "placeholder"], requireAll = false)
fun imageUri(imageView: ImageView, imageUri: Uri?, placeholder: Drawable?) {
    when (imageUri) {
        null -> {
            Timber.d("Unsetting image url")
            Glide.with(imageView)
                .load(placeholder)
                .into(imageView)
        }
        else -> {
            Glide.with(imageView)
                .load(imageUri)
                .apply(RequestOptions().placeholder(placeholder))
                .into(imageView)
        }
    }
}

@BindingAdapter(value = ["defaultImageUrl", "placeholder"], requireAll = false)
fun defaultImageUrl(imageView: ImageView, imageUrl: String?, placeholder: Drawable?) {
    imageUri(imageView, imageUrl?.toUri(), placeholder)
}

@BindingAdapter("truncate")
fun bindTextView(textView: TextView, description: String) {
    var listDescription = description
    if (listDescription.length > 150) {
        listDescription = listDescription.substring(0, 150)
    }
    textView.text = "$listDescription..."
}

@BindingAdapter("convertLong")
fun bindTextView(textView: TextView, long: Long) {
    textView.text = long.toString()
}

@BindingAdapter("playerNames")
fun bindTextViewName(textView: TextView, name: String?) {
    textView.text = if (name.isNullOrEmpty()) {
        "anonymous"
    } else {
        name
    }
}

@BindingAdapter("levelRate")
fun bindRatingBar(ratingBar: RatingBar, level: String?) {
    ratingBar.rating = when (level?.toUpperCase(Locale.ROOT)) {
        BEGINNER.toString() -> 1.0f
        else -> 0f
    }
}