package com.heathkev.quizado.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.heathkev.quizado.data.User
import com.heathkev.quizado.ui.MainNavigationFragment
import com.heathkev.quizado.databinding.FragmentDetailBinding
import com.heathkev.quizado.utils.doOnApplyWindowInsets
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentDetailBinding

    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val quizData = DetailFragmentArgs.fromBundle(requireArguments()).quizData

        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = if(firebaseAuth.currentUser != null){
            val authUser = firebaseAuth.currentUser!!
            User(
                authUser.uid,
                authUser.displayName,
                authUser.photoUrl,
                authUser.email
            )
        }else{
            User()
        }
        detailViewModel.setQuizDetail(quizData, currentUser)

        binding = FragmentDetailBinding.inflate(inflater, container, false)
            .apply {
                viewModel = detailViewModel
                lifecycleOwner = viewLifecycleOwner
            }

        binding.detailsStartBtn.setOnClickListener{
            detailViewModel.startQuiz(quizData)
        }

        detailViewModel.startQuizData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                this.findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToQuizFragment(quizData))
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
