package com.heathkev.quizado.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.heathkev.quizado.ui.MainNavigationFragment
import com.heathkev.quizado.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val model: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentHomeBinding.inflate(inflater, container, false)
                .apply{
                lifecycleOwner = viewLifecycleOwner
                viewModel = model
            }

        val adapter = HomeResultsListAdapter()
        val listView = binding.homeRecentResultsList
        listView.adapter = adapter

        model.resultList.observe(viewLifecycleOwner, Observer {
            it.let {
                adapter.submitList(it)
            }
        })

        binding.homePlayButton.setOnClickListener {
            model.playQuiz()
        }

        model.navigateToQuizListModel.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                this.findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToDetailFragment(
                        it
                    )
                )
                model.playQuizComplete()
            }
        })

        return binding.root
    }
}