package com.heathkev.quizapp

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.heathkev.quizapp.data.QuizListModel
import com.heathkev.quizapp.ui.list.QuizListAdapter

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<QuizListModel>?) {
    val adapter = recyclerView.adapter as QuizListAdapter
    adapter.submitList(data)
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                .placeholder(R.drawable.placeholder_image))
            .into(imgView)
    }
}

@BindingAdapter("truncate")
fun bindTextView(textView: TextView, description: String)
{
    var listDescription = description
    if(listDescription.length > 150){
        listDescription = listDescription.substring(0,150)
    }
    textView.text = "${listDescription}..."
}

@BindingAdapter("convertLong")
fun bindTextView(textView: TextView, long: Long)
{
    textView.text = long.toString()
}
