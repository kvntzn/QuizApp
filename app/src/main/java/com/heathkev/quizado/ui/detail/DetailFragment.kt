package com.heathkev.quizado.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePaddingRelative
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.heathkev.quizado.MainNavigationFragment
import com.heathkev.quizado.R
import com.heathkev.quizado.data.User
import com.heathkev.quizado.databinding.FragmentDetailBinding
import com.heathkev.quizado.utils.doOnApplyWindowInsets

class DetailFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_detail, container, false)

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

        val viewModelFactory = DetailViewModelFactory(quizData, currentUser)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.detailsStartBtn.setOnClickListener{
            viewModel.startQuiz(quizData)
        }

        viewModel.startQuizData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                this.findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToQuizFragment(quizData))
                viewModel.startQuizComplete()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.doOnApplyWindowInsets { _, insets, _ ->
            binding.statusBar.run {
                layoutParams.height = insets.systemWindowInsetTop
                isVisible = layoutParams.height > 0
                requestLayout()
            }
        }

        binding.detailsScroll.doOnApplyWindowInsets { v, insets, padding ->
            v.updatePaddingRelative(bottom = padding.bottom + insets.systemWindowInsetBottom)
        }
    }
}
