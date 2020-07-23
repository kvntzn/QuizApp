package com.heathkev.quizado.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.heathkev.quizado.data.Result
import com.heathkev.quizado.databinding.HomeResultSingleListItemBinding

class HomeResultsListAdapter : ListAdapter<Result, HomeResultsListAdapter.ResultsViewHolder>(DiffCallback){

    class ResultsViewHolder(private var binding: HomeResultSingleListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result: Result) {
            binding.resultModel = result

            val total = result.correct + result.wrong + result.wrong

            binding.homeResultScore.text = "${result.correct}/$total"

            val percentage = result.correct * 100
            binding.homeResultProgress.progress = percentage.toInt()
            binding.homeResultsPercent.text = "$percentage%"

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        val view = HomeResultSingleListItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ResultsViewHolder(view)
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