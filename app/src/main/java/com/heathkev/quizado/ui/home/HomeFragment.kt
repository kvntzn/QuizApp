package com.heathkev.quizado.ui.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.heathkev.quizado.databinding.FragmentHomeBinding
import com.heathkev.quizado.ui.MainActivityViewModel
import com.heathkev.quizado.ui.MainNavigationFragment
import com.heathkev.quizado.ui.signin.setupProfileMenuItem
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class HomeFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentHomeBinding.inflate(inflater, container, false)
                .apply{
                lifecycleOwner = viewLifecycleOwner
                viewModel = homeViewModel
            }

        (activity as AppCompatActivity?)!!.apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        val quizAdapter = HomeQuizListAdapter(HomeQuizListAdapter.OnClickListener {
            Timber.d("Quiz Clicked")
            this.findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(
                    it
                )
            )
        })
        binding.homeForYouList.adapter = quizAdapter
        binding.homeTrendingList.adapter = quizAdapter
        homeViewModel.quizList.observe(viewLifecycleOwner, Observer {
            it.let{
                quizAdapter.submitList(it)
            }
        })

//        val adapter = HomeResultsListAdapter()
//        binding.homeRecentResultsList.adapter = adapter
//        homeViewModel.resultList.observe(viewLifecycleOwner, Observer {
//            it.let {
//                adapter.submitList(it)
//            }
//        })

        binding.homePlayButton.setOnClickListener {
            homeViewModel.playQuiz()
        }

        homeViewModel.navigateToQuizListModel.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                this.findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToDetailFragment(
                        it
                    )
                )
                homeViewModel.playQuizComplete()
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