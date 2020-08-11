package com.heathkev.quizado.ui.result

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.app.ShareCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.heathkev.quizado.ui.MainNavigationFragment
import com.heathkev.quizado.R
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.data.User
import com.heathkev.quizado.databinding.FragmentResultBinding
import com.heathkev.quizado.utils.doOnApplyWindowInsets

class ResultFragment : MainNavigationFragment() {

    private var correct: Int = 0
    private lateinit var quizData: QuizListModel
    private lateinit var binding: FragmentResultBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_result, container, false)

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

        binding.root.doOnApplyWindowInsets { _, insets, _ ->
            binding.statusBar.run {
                layoutParams.height = insets.systemWindowInsetTop
                isVisible = layoutParams.height > 0
                requestLayout()
            }
        }

        binding.resultsShareBtn.setOnClickListener{
            shareSuccess()
        }

        binding.resultsPlayBtn.setOnClickListener {
            this.findNavController().navigate(ResultFragmentDirections.actionResultFragmentToDetailFragment(quizData))
        }

        binding.resultsHomeBtn.setOnClickListener {
            this.findNavController().navigateUp()
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
