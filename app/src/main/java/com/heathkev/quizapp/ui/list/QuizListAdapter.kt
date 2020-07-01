package com.heathkev.quizapp.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.heathkev.quizapp.data.QuizListModel
import com.heathkev.quizapp.databinding.SingleListItemBinding
import kotlinx.android.synthetic.main.single_list_item.view.*

class QuizListAdapter(val onClickListener: OnClickListener) : ListAdapter<QuizListModel, QuizListAdapter.QuizViewHolder>(DiffCallback){

    class QuizViewHolder(private var binding: SingleListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(quizListModel: QuizListModel) {
            binding.quizListModel = quizListModel
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = SingleListItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quizListModel = getItem(position)
        holder.itemView.list_btn.setOnClickListener {
            onClickListener.onClick(position)
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

    class OnClickListener(val clickListener: (position: Int) -> Unit) {
        fun onClick(position: Int) = clickListener(position)
    }
}

