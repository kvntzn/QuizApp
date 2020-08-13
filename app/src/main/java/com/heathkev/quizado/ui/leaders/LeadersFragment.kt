package com.heathkev.quizado.ui.leaders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.heathkev.quizado.databinding.FragmentLeadersBinding
import com.heathkev.quizado.ui.MainNavigationFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeadersFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentLeadersBinding

    private val leadersViewModel: LeadersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeadersBinding.inflate(inflater, container, false)
            .apply {
                viewModel = leadersViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        val adapter = LeadersListAdapter()
        val listView = binding.leadersList
        listView.setHasFixedSize(true)
        listView.adapter = adapter

        leadersViewModel.results.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}