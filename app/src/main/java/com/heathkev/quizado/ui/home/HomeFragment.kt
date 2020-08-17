package com.heathkev.quizado.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.heathkev.quizado.R
import com.heathkev.quizado.ui.MainNavigationFragment
import com.heathkev.quizado.databinding.FragmentHomeBinding
import com.heathkev.quizado.ui.MainActivityViewModel
import com.heathkev.quizado.utils.asGlideTarget
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val mainActivityViewModel: MainActivityViewModel by viewModels()
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

        //TODO: Create extension for this
        mainActivityViewModel.currentUser.observe(viewLifecycleOwner, Observer {
            if(it!= null){
                when (it.photoUrl) {
                    null -> {
                        Glide.with(binding.toolbar.context)
                            .load(R.drawable.ic_default_profile_avatar)
                            .apply(RequestOptions.circleCropTransform())
                            .into(binding.toolbar.asGlideTarget())
                    }
                    else -> {
                        Glide.with(binding.toolbar.context)
                            .load(it.photoUrl)
                            .apply(RequestOptions.circleCropTransform())
                            .into(binding.toolbar.asGlideTarget())
                    }
                }
            }
        })

        return binding.root
    }
}