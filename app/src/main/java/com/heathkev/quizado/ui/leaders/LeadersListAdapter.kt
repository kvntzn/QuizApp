package com.heathkev.quizado.ui.leaders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.heathkev.quizado.databinding.ItemLeaderboardsBinding
import com.heathkev.quizado.model.Leaderboard

class LeadersListAdapter : ListAdapter<Leaderboard, LeadersListAdapter.LeadersViewHolder>(DiffCallback){

    class LeadersViewHolder(private var binding: ItemLeaderboardsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(leaderBoard: Leaderboard) {
            binding.leaderBoard = leaderBoard
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeadersViewHolder {
        val view = ItemLeaderboardsBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return LeadersViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeadersViewHolder, position: Int) {
        val result = getItem(position)
        holder.bind(result)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Leaderboard>() {
        override fun areItemsTheSame(oldItem: Leaderboard, newItem: Leaderboard): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Leaderboard, newItem: Leaderboard): Boolean {
            return oldItem.userId == newItem.userId
        }
    }
}