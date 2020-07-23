package com.heathkev.quizado.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.heathkev.quizado.R
import com.heathkev.quizado.databinding.FragmentHomeBinding
import com.heathkev.quizado.ui.leaders.LeadersListAdapter
import com.heathkev.quizado.ui.leaders.LeadersViewModel

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = HomeResultsListAdapter()
        val listView = binding.homeRecentResultsList
        listView.adapter = adapter

        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
//        val listProgress = binding.leadersProgress

        viewModel.resultList.observe(viewLifecycleOwner, Observer {
            it?.let {
                listView.startAnimation(fadeInAnimation)
//                listProgress.startAnimation(fadeOutAnimation)

                adapter.submitList(it)
            }
        })

        return binding.root
    }
}