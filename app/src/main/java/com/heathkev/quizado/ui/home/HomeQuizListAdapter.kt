package com.heathkev.quizado.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.heathkev.quizado.model.QuizListModel
import com.heathkev.quizado.databinding.ItemHomeQuizBinding
import kotlinx.android.synthetic.main.item_quiz.view.*

class HomeQuizListAdapter(private val onClickListener: OnClickListener) : ListAdapter<QuizListModel, HomeQuizListAdapter.HomeQuizViewHolder>(DiffCallback){

    class HomeQuizViewHolder(private var binding: ItemHomeQuizBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(quizListModel: QuizListModel) {
            binding.quizListModel = quizListModel
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeQuizViewHolder {
        val view = ItemHomeQuizBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return HomeQuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeQuizViewHolder, position: Int) {
        val quizListModel = getItem(position)
        holder.itemView.quiz_card.setOnClickListener {
            onClickListener.onClick(quizListModel)
        }
        holder.bind(quizListModel)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<QuizListModel>() {
        override fun areItemsTheSame(oldItem: QuizListModel, newItem: QuizListModel): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: QuizListModel, newItem: QuizListModel): Boolean {
            return oldItem.quiz_id == newItem.quiz_id
        }
    }

    class OnClickListener(val clickListener: (quizListModel: QuizListModel) -> Unit) {
        fun onClick(quizListModel: QuizListModel) = clickListener(quizListModel)
    }
}