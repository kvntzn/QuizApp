package com.heathkev.quizado.ui.profile

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.heathkev.quizado.databinding.FragmentProfileBinding
import com.heathkev.quizado.ui.MainActivityViewModel
import com.heathkev.quizado.ui.MainNavigationFragment
import com.heathkev.quizado.ui.signin.setupProfileMenuItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentProfileBinding

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        profileViewModel.user.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                profileViewModel.getResult(it)
            }
        })

        binding = FragmentProfileBinding.inflate(inflater, container, false).apply {
            viewModel = profileViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        (activity as AppCompatActivity?)!!.apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        val adapter = ProfileResultsListAdapter()
        binding.profileRecentResultsList.adapter = adapter
        profileViewModel.resultList.observe(viewLifecycleOwner, Observer {
            it.let {
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