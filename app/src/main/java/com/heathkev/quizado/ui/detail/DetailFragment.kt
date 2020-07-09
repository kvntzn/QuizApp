package com.heathkev.quizado.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.heathkev.quizado.R
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.databinding.FragmentDetailBinding
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.ui.list.QuizListViewModel
import kotlinx.android.synthetic.main.fragment_detail.*

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {

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

    private fun loadResultData(quizData: QuizListModel) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUserId = if(firebaseAuth.currentUser != null){
            firebaseAuth.currentUser!!.uid
        }else{
            ""
            // go to home page
        }

        val firebaseRepository = FirebaseRepository()
        firebaseRepository.getResults(quizData.quiz_id).document(currentUserId).get().addOnCompleteListener{
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

                       binding.detailsScoreText.text = "$percent%"
                   }
                }else{
                    // Document doesn't exist, and result should stay NA
                }
            }
        }
    }

}
