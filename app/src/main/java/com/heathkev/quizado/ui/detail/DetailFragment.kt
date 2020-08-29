package com.heathkev.quizado.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.heathkev.quizado.databinding.FragmentDetailBinding
import com.heathkev.quizado.ui.MainNavigationFragment
import com.heathkev.quizado.utils.doOnApplyWindowInsets
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentDetailBinding

    private val args: DetailFragmentArgs by navArgs()
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        detailViewModel.currentUser.observe(viewLifecycleOwner, Observer {user ->
            if(user != null){
                detailViewModel.setQuizDetail(args.quizData, user)
            }
        })

        binding = FragmentDetailBinding.inflate(inflater, container, false)
            .apply {
                viewModel = detailViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        binding.detailsStartBtn.setOnClickListener{
            detailViewModel.startQuiz(args.quizData)
        }

        detailViewModel.startQuizData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                this.findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToQuizFragment(args.quizData))
                detailViewModel.startQuizComplete()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.detailsScroll.doOnApplyWindowInsets { v, insets, padding ->
            v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
        }
    }
}
