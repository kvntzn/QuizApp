package com.heathkev.quizado.ui.result

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.heathkev.quizado.R
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.ui.detail.DetailFragmentDirections
import kotlinx.android.synthetic.main.fragment_result.*

class ResultFragment : Fragment() {

    private lateinit var quizData: QuizListModel
    private var correct: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        quizData = ResultFragmentArgs.fromBundle(
            requireArguments()
        ).quizData

        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        results_share_btn.setOnClickListener{
            shareSuccess()
        }

        results_play_btn.setOnClickListener {
            it.findNavController().navigate(ResultFragmentDirections.actionResultFragmentToDetailFragment(quizData))
        }

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
                    correct = result.getLong("correct")!!
                    val wrong = result.getLong("wrong")!!
                    val missed = result.getLong("unanswered")!!

                    val total = correct + wrong + missed
                    results_score.text = getString(R.string.score_over, correct, total)

                    // calculate progress
                    val percent = (correct*100)/total

                    results_percent.text = getString(R.string.score_percentage, percent)
                    results_progress.progress = percent.toInt()

                    val image: Int
                    val passed = correct > (total / 2)
                    if(passed){
                        results_title.text = getString(R.string.congratulations)
                        results_title.setTextColor(resources.getColor(R.color.colorCorrect, null))

                        result_message_text.text =  getString(R.string.congrats_message, quizData.name)
                        image = R.drawable.ic_success
                    }else{
                        results_title.text = getString(R.string.try_again)
                        results_title.setTextColor(resources.getColor(R.color.colorWrong, null))

                         image = R.drawable.ic_failed
                        result_message_text.text = getString(R.string.failed_message, quizData.name)
                    }

                    Glide.with(this)
                        .load(image)
                        .into(result_image)
                }
            }
        }


    }

    private fun getShareIntent() : Intent {
        return ShareCompat.IntentBuilder.from(requireActivity())
            .setText(getString(R.string.share_text, correct, quizData.questions, quizData.name))
            .setType("text/plain")
            .intent

//        val shareIntent = Intent(Intent.ACTION_SEND)
//        shareIntent.setType("text/plain")
//                .putExtra(Intent.EXTRA_TEXT,
//                        getString(R.string.share_success_text, args.numCorrect,
//                                args.numQuestions))
//        return  shareIntent
    }

    private fun shareSuccess(){
        startActivity(getShareIntent())
    }
}
