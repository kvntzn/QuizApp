package com.heathkev.quizado.ui.leaders

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.heathkev.quizado.R
import com.heathkev.quizado.databinding.FragmentLeadersBinding
import com.heathkev.quizado.databinding.FragmentListBinding
import com.heathkev.quizado.ui.list.QuizListViewModel

class LeadersFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentLeadersBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_leaders, container, false
        )

        val viewModel = ViewModelProvider(this).get(LeadersViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = LeadersListAdapter()
        val listView = binding.leadersList
        listView.setHasFixedSize(true)
        listView.adapter = adapter

        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        val listProgress = binding.leadersProgress

        viewModel.results.observe(viewLifecycleOwner, Observer {
            it?.let {
                listView.startAnimation(fadeInAnimation)
                listProgress.startAnimation(fadeOutAnimation)

                adapter.submitList(it)
            }
        })

        return binding.root
    }
}