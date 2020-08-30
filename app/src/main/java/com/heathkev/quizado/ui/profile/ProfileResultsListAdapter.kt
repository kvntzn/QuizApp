package com.heathkev.quizado.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.heathkev.quizado.R
import com.heathkev.quizado.data.Result
import com.heathkev.quizado.databinding.HomeResultSingleListItemBinding

class ProfileResultsListAdapter() : ListAdapter<Result, ProfileResultsListAdapter.ResultsViewHolder>(
    DiffCallback
){

    class ResultsViewHolder(private var binding: HomeResultSingleListItemBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result: Result) {
            binding.resultModel = result

            val correct = result.correct
            val wrong = result.wrong
            val unanswered = result.unanswered

            val total = correct + unanswered + wrong

            if(correct != 0L){
                binding.homeResultScore.text =  context.getString(R.string.score_over, correct, total)

                val percentage = (correct * 100)/ total
                binding.homeResultProgress.progress = percentage.toInt()
                binding.homeResultsPercent.text = context.getString(R.string.score_percentage, percentage)
            }else{
                binding.homeResultScore.text = context.getString(R.string.empty)
                binding.homeResultProgress.progress = 0
                binding.homeResultsPercent.text = context.getString(R.string.empty)
            }

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        val view = HomeResultSingleListItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ResultsViewHolder(
            view,
            parent.context
        )
    }

    override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
        val result = getItem(position)
        holder.bind(result)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.user_id == newItem.user_id
        }
    }
}