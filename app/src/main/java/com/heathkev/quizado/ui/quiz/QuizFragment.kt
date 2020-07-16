package com.heathkev.quizado.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.heathkev.quizado.R
import com.heathkev.quizado.data.User
import com.heathkev.quizado.databinding.FragmentQuizBinding
import kotlinx.android.synthetic.main.fragment_quiz.*

class QuizFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentQuizBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_quiz, container, false)

        val quizData = QuizFragmentArgs.fromBundle(requireArguments()).quizData

        val firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = if(firebaseAuth.currentUser != null){
            val authUser = firebaseAuth.currentUser!!
           User(
               authUser.uid,
               authUser.displayName.toString(),
               authUser.photoUrl.toString(),
               authUser.email.toString()
           )
        }else{
            User()
            // go to home page
        }

        val viewModelFactory = QuizViewModelFactory(quizData, currentUser)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(QuizViewModel::class.java)
        binding.quizViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.questionNumber.observe(viewLifecycleOwner, Observer {
            (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_quiz_question, it,  quizData.questions)
        })

        setButtonVisibility(binding.quizOptionA,View.VISIBLE, true)
        setButtonVisibility(binding.quizOptionB,View.VISIBLE, true)
        setButtonVisibility(binding.quizOptionC,View.VISIBLE, true)
        setButtonVisibility(binding.quizNextBtn,View.INVISIBLE, false)

        binding.quizOptionA.setOnClickListener{
            val btn = it as Button

            validateOptionSelected(viewModel, btn, binding)
        }

        binding.quizOptionB.setOnClickListener{
            val btn = it as Button

            validateOptionSelected(viewModel, btn, binding)
        }

        binding.quizOptionC.setOnClickListener{
            val btn = it as Button

            validateOptionSelected(viewModel, btn, binding)
        }

        binding.quizOptionD.setOnClickListener{
            val btn = it as Button

            validateOptionSelected(viewModel, btn, binding)
        }


        binding.quizNextBtn.setOnClickListener{
            viewModel.loadNextQuestion()
            resetOptions(binding)
        }

        viewModel.isTimeUp.observe(viewLifecycleOwner, Observer {
            if(it){

                // Empty string no answer was selected
                val correctAnswer = viewModel.getCorrectAnswer("")
                highlightCorrectAnswer(correctAnswer)
                setButtonVisibility(binding.quizNextBtn, View.VISIBLE, true)
                viewModel.onTimeUpComplete()
            }
        })

        viewModel.shouldNavigateToResult.observe(viewLifecycleOwner, Observer {
            if(it){
                this.findNavController().navigate(QuizFragmentDirections.actionQuizFragmentToResultFragment(quizData))
                viewModel.navigateToResultPageComplete()
            }
        })

        return binding.root
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

    private fun validateOptionSelected(viewModel: QuizViewModel, option: Button, binding: FragmentQuizBinding){
        if(viewModel.canAnswer()){
            val correctAnswer = viewModel.getCorrectAnswer(option.text.toString())

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
