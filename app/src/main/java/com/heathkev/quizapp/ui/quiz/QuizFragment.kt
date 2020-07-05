package com.heathkev.quizapp.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.heathkev.quizapp.R
import com.heathkev.quizapp.databinding.FragmentQuizBinding

/**
 * A simple [Fragment] subclass.
 */
class QuizFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentQuizBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_quiz, container, false)

        val quizData = QuizFragmentArgs.fromBundle(requireArguments()).quizData

        val viewModelFactory = QuizViewModelFactory(quizData)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(QuizViewModel::class.java)
        binding.quizViewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

}
