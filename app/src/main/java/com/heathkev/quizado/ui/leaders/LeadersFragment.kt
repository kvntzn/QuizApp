package com.heathkev.quizado.ui.leaders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.heathkev.quizado.ui.MainNavigationFragment
import com.heathkev.quizado.R
import com.heathkev.quizado.databinding.FragmentLeadersBinding

class LeadersFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentLeadersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_leaders, container, false)

        val viewModel = ViewModelProvider(this).get(LeadersViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = LeadersListAdapter()
        val listView = binding.leadersList
        listView.setHasFixedSize(true)
        listView.adapter = adapter

        viewModel.results.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }
}