package com.heathkev.quizado.ui.result

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.heathkev.quizado.R
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.data.User
import com.heathkev.quizado.databinding.FragmentQuizBinding
import com.heathkev.quizado.databinding.FragmentResultBinding
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.ui.detail.DetailFragmentDirections
import com.heathkev.quizado.ui.quiz.QuizViewModel
import com.heathkev.quizado.ui.quiz.QuizViewModelFactory
import kotlinx.android.synthetic.main.fragment_result.*

class ResultFragment : Fragment() {

    private lateinit var quizData: QuizListModel
    private var correct: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentResultBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_result, container, false)

        quizData = ResultFragmentArgs.fromBundle(
            requireArguments()
        ).quizData

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

        val application = requireNotNull(activity).application
        val viewModelFactory = ResultViewModelFactory(application, currentUser, quizData)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(ResultViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)

        viewModel.correctScore.observe(viewLifecycleOwner, Observer {
            binding.resultsContent.startAnimation(fadeInAnimation)
            binding.resultLoadProgress.startAnimation(fadeOutAnimation)

            correct = it
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        results_share_btn.setOnClickListener{
            shareSuccess()
        }

        results_play_btn.setOnClickListener {
            this.findNavController().navigate(ResultFragmentDirections.actionResultFragmentToDetailFragment(quizData))
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
