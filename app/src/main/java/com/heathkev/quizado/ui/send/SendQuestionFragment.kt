package com.heathkev.quizado.ui.send

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.heathkev.quizado.R
import com.heathkev.quizado.data.QuestionsModel
import com.heathkev.quizado.databinding.FragmentDetailBinding
import com.heathkev.quizado.databinding.FragmentSendQuestionBinding

class SendQuestionFragment : Fragment() {

    private val viewModel: SendQuestionViewModel by lazy {
        ViewModelProvider(this).get(SendQuestionViewModel::class.java)
    }

    private lateinit var binding: FragmentSendQuestionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_send_question, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.showSnackBarEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.invalid_question),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()

                viewModel.doneShowingSnackbar()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.questionSubmitBtn.setOnClickListener {

            val questionTitle = binding.questionTitleLayout.editText!!.text.toString()
            val correctAnswer = binding.questionCorrectAnswerLayout.editText!!.text.toString()
            val wrongAnswer = binding.questionWrongAnswerLayout.editText!!.text.toString()
            val wrongAnswer2 = binding.questionWrongAnswer2Layout.editText!!.text.toString()
            val wrongAnswer3 = binding.questionWrongAnswer3Layout.editText!!.text.toString()

            val question = QuestionsModel(
                "",
                questionTitle,
                correctAnswer,
                wrongAnswer,
                wrongAnswer2,
                wrongAnswer3,
                correctAnswer,
                0L
            )

            viewModel.onSubmitQuestion(question, binding.questionCategory.selectedItem.toString())
        }
    }
}