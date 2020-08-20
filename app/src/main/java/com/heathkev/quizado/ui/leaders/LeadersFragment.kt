package com.heathkev.quizado.ui.leaders

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.heathkev.quizado.databinding.FragmentLeadersBinding
import com.heathkev.quizado.ui.MainActivityViewModel
import com.heathkev.quizado.ui.MainNavigationFragment
import com.heathkev.quizado.ui.signin.setupProfileMenuItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeadersFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentLeadersBinding

    private val mainActivityViewModel: MainActivityViewModel by viewModels()
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

        (activity as AppCompatActivity?)!!.apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
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

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        binding.toolbar.setupProfileMenuItem(menu, inflater, mainActivityViewModel, this)
    }
}