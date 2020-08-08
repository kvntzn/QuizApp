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
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.databinding.FragmentDetailBinding
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.ui.list.QuizListViewModel
import com.heathkev.quizado.utils.doOnApplyWindowInsets

class DetailFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_detail, container, false)
        binding.lifecycleOwner = this

        val quizData = DetailFragmentArgs.fromBundle(requireArguments()).quizData
        binding.quizListModel = quizData

        val viewModel = ViewModelProvider(this).get(QuizListViewModel::class.java)

        binding.detailsStartBtn.setOnClickListener{
            viewModel.displayQuizListModelDetails(quizData)
        }

        viewModel.navigateToSelectedQuizListModelPosition.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                this.findNavController().navigate(DetailFragmentDirections.actionDetailFragmentToQuizFragment(quizData))
                viewModel.displayQuizListModelDetailsComplete()
            }
        })

        loadResultData(quizData)
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

    private fun loadResultData(quizData: QuizListModel) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUserId = if(firebaseAuth.currentUser != null){
            firebaseAuth.currentUser!!.uid
        }else{
            ""
            // go to home page
        }

        val firebaseRepository = FirebaseRepository()
        firebaseRepository.getResultsByQuizId(quizData.quiz_id).document(currentUserId).get().addOnCompleteListener{
            if(it.isSuccessful){
                val result = it.result

                if (result != null) {
                    val correct = result.getLong("correct")
                    val wrong = result.getLong("wrong")
                    val missed = result.getLong("unanswered")

                   if(correct!= null && wrong != null && missed != null){
                       // calculate progress
                       val total = correct + wrong + missed
                       val percent = (correct*100)/total

                       binding.detailsScoreText.text = getString(R.string.score_percentage, percent)
                   }
                }else{
                    // Document doesn't exist, and result should stay NA
                }
            }
        }
    }

}
