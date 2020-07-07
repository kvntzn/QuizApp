package com.heathkev.quizapp.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.heathkev.quizapp.R
import com.heathkev.quizapp.firebase.FirebaseRepository
import kotlinx.android.synthetic.main.fragment_result.*

class ResultFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        results_home_btn.setOnClickListener{
            Toast.makeText(context,"Button clicked", Toast.LENGTH_SHORT).show()
        }

        val quizData = ResultFragmentArgs.fromBundle(
            requireArguments()
        ).quizData

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
                    val correct = result.getLong("correct")!!
                    val wrong = result.getLong("wrong")!!
                    val missed = result.getLong("unanswered")!!

                    results_correct_text.text = correct.toString()
                    results_wrong_text.text = wrong.toString()
                    results_missed_text.text = missed.toString()

                    // calculate progress
                    val total = correct + wrong + missed
                    val percent = (correct*100)/total

                    results_percent.text = "$percent%"
                    results_progress.progress = percent.toInt()
                }
            }
        }
    }
}
