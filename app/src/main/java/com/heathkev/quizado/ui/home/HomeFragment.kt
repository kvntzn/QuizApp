package com.heathkev.quizado.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.heathkev.quizado.R
import com.heathkev.quizado.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        val viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = HomeResultsListAdapter()
        val listView = binding.homeRecentResultsList
        listView.adapter = adapter

        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        val listProgress = binding.homeProgress


        viewModel.resultList.observe(viewLifecycleOwner, Observer {
            it?.let {
                listView.startAnimation(fadeInAnimation)
                listProgress.startAnimation(fadeOutAnimation)

                adapter.submitList(it)
            }
        })

        binding.homePlayButton.setOnClickListener {
            viewModel.playQuiz()
        }

        viewModel.navigateToQuizListModel.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                this.findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToQuizFragment(
                        it
                    )
                )
                viewModel.playQuizComplete()
            }
        })

        return binding.root
    }
}