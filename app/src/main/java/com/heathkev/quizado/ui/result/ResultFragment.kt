package com.heathkev.quizado.ui.result

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.app.ShareCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.heathkev.quizado.R
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.databinding.FragmentResultBinding
import com.heathkev.quizado.ui.MainNavigationFragment
import com.heathkev.quizado.utils.doOnApplyWindowInsets
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : MainNavigationFragment() {

    private var correct: Int = 0
    private lateinit var binding: FragmentResultBinding

    private val args: ResultFragmentArgs by navArgs()
    private val resultViewModel: ResultViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        resultViewModel.currentUser.observe(viewLifecycleOwner, Observer {user ->
            if(user != null){
                resultViewModel.fetchQuizResult(args.quizData, user)
            }
        })

        binding = FragmentResultBinding.inflate(inflater, container, false).apply {
            viewModel = resultViewModel
            lifecycleOwner = viewLifecycleOwner
            quizDetail = args.quizData
        }

        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)

        resultViewModel.correctScore.observe(viewLifecycleOwner, Observer {
            binding.resultsContent.startAnimation(fadeInAnimation)
            binding.resultLoadProgress.startAnimation(fadeOutAnimation)

            binding.resultsScore.text = getString(R.string.score_over, it, args.quizData.questions)

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
            this.findNavController().navigate(ResultFragmentDirections.actionResultFragmentToDetailFragment(args.quizData))
        }

        binding.resultsHomeBtn.setOnClickListener {
            this.findNavController().navigateUp()
        }
    }

    private fun getShareIntent() : Intent {
        return ShareCompat.IntentBuilder.from(requireActivity())
            .setText(getString(R.string.share_text, correct, args.quizData.questions, args.quizData.name))
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
