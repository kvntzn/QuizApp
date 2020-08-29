package com.heathkev.quizado.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.heathkev.quizado.R
import com.heathkev.quizado.databinding.FragmentQuizBinding
import com.heathkev.quizado.ui.MainNavigationFragment
import com.heathkev.quizado.utils.doOnApplyWindowInsets
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_quiz.*

@AndroidEntryPoint
class QuizFragment : MainNavigationFragment() {

    private lateinit var binding: FragmentQuizBinding

    private val args: QuizFragmentArgs by navArgs()
    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        quizViewModel.user.observe(viewLifecycleOwner, Observer { user ->
            quizViewModel.initializeQuestions(args.quizData, user)
        })

        binding = FragmentQuizBinding.inflate(inflater, container, false)
            .apply {
            viewModel = quizViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setButtonVisibility(binding.quizOptionA,View.VISIBLE, true)
        setButtonVisibility(binding.quizOptionB,View.VISIBLE, true)
        setButtonVisibility(binding.quizOptionC,View.VISIBLE, true)
        setButtonVisibility(binding.quizNextBtn,View.INVISIBLE, false)

        binding.quizOptionA.setOnClickListener{
            val btn = it as Button

            validateOptionSelected(quizViewModel, btn, binding)
        }

        binding.quizOptionB.setOnClickListener{
            val btn = it as Button

            validateOptionSelected(quizViewModel, btn, binding)
        }

        binding.quizOptionC.setOnClickListener{
            val btn = it as Button

            validateOptionSelected(quizViewModel, btn, binding)
        }

        binding.quizOptionD.setOnClickListener{
            val btn = it as Button

            validateOptionSelected(quizViewModel, btn, binding)
        }

        binding.quizNextBtn.setOnClickListener{
            quizViewModel.loadNextQuestion()
            resetOptions(binding)
        }

        quizViewModel.isTimeUp.observe(viewLifecycleOwner, Observer {
            if(it){

                // Empty string no answer was selected
                val correctAnswer = quizViewModel.getCorrectAnswer("")
                highlightCorrectAnswer(correctAnswer)
                setButtonVisibility(binding.quizNextBtn, View.VISIBLE, true)
                quizViewModel.onTimeUpComplete()
            }
        })

        quizViewModel.shouldNavigateToResult.observe(viewLifecycleOwner, Observer {
            if(it){
                this.findNavController().navigate(QuizFragmentDirections.actionQuizFragmentToResultFragment(args.quizData))
                quizViewModel.navigateToResultPageComplete()
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
    }

    private fun resetOptions(binding: FragmentQuizBinding) {
        setButtonBackground(binding.quizOptionA, null)
        setButtonBackground(binding.quizOptionB, null)
        setButtonBackground(binding.quizOptionC, null)
        setButtonBackground(binding.quizOptionD, null)

        binding.quizOptionA.setTextColor(resources.getColor(R.color.colorOnBackground, null))
        binding.quizOptionB.setTextColor(resources.getColor(R.color.colorOnBackground, null))
        binding.quizOptionC.setTextColor(resources.getColor(R.color.colorOnBackground, null))
        binding.quizOptionD.setTextColor(resources.getColor(R.color.colorOnBackground, null))

        setButtonVisibility(binding.quizNextBtn, View.INVISIBLE, false)
    }

    private fun validateOptionSelected(quizViewModel: QuizViewModel, option: Button, binding: FragmentQuizBinding){
        if(quizViewModel.canAnswer()){
            val correctAnswer = quizViewModel.getCorrectAnswer(option.text.toString())

            option.setTextColor(resources.getColor(R.color.primaryTextColor, null))

            val isCorrect = option.text == correctAnswer
            setButtonBackground(option, isCorrect)
            if(!isCorrect){
                highlightCorrectAnswer(correctAnswer)
            }

            setButtonVisibility(binding.quizNextBtn,View.VISIBLE,true)
        }
    }

    private fun highlightCorrectAnswer(correctAnswer: String) {
        when(correctAnswer){
            quiz_option_a.text -> {
                setButtonBackground(quiz_option_a, true)
                quiz_option_a.setTextColor(resources.getColor(R.color.primaryTextColor, null))
            }
            quiz_option_b.text -> {
                setButtonBackground(quiz_option_b, true)
                quiz_option_b.setTextColor(resources.getColor(R.color.primaryTextColor, null))
            }
            quiz_option_c.text -> {
                setButtonBackground(quiz_option_c, true)
                quiz_option_c.setTextColor(resources.getColor(R.color.primaryTextColor, null))
            }
            else -> {
                setButtonBackground(quiz_option_d, true)
                quiz_option_d.setTextColor(resources.getColor(R.color.primaryTextColor, null))
            }
        }
    }

    private fun setButtonVisibility(button: Button, visibility: Int, isEnabled: Boolean){
        button.visibility = visibility
        button.isEnabled = isEnabled
    }

    private fun setButtonBackground(button: Button, isCorrect: Boolean?){
        if(isCorrect != null && isCorrect){
            button.backgroundTintList = resources.getColorStateList(R.color.colorCorrect, null)
        }else if(isCorrect != null && !isCorrect){
            button.backgroundTintList = resources.getColorStateList(R.color.colorWrong, null)
        }else{
            button.backgroundTintList = resources.getColorStateList(R.color.colorBackground, null)
        }
    }



}
