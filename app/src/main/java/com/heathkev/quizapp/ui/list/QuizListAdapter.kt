package com.heathkev.quizapp.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.heathkev.quizapp.R
import com.heathkev.quizapp.data.QuizListModel

class QuizListAdapter : RecyclerView.Adapter<QuizListAdapter.QuizViewHolder>(){

    var quizListModels = listOf<QuizListModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_list_item, parent, false)
        return QuizViewHolder(view)
    }

    override fun getItemCount(): Int {
        return quizListModels.size
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.listTitle.text = quizListModels[position].name

        val imageUrl = quizListModels[position].image
        Glide
            .with(holder.itemView.context)
            .load(imageUrl)
            .centerCrop()
            .placeholder(R.drawable.placeholder_image)
            .into(holder.listImage);

        var listDescription = quizListModels[position].desc
        if(listDescription.length > 150){
            listDescription = listDescription.substring(0,150)
        }
        holder.listDesc.text = "$listDescription..."
        holder.listLevel.text = quizListModels[position].level
    }

    class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val listImage: ImageView = itemView.findViewById(R.id.list_image)
        val listTitle: TextView = itemView.findViewById(R.id.list_title)
        val listDesc: TextView = itemView.findViewById(R.id.list_desc)
        val listLevel: TextView = itemView.findViewById(R.id.list_difficulty)
        val listBtn: Button = itemView.findViewById(R.id.list_btn)
    }

}