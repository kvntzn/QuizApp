package com.heathkev.quizado.ui.send

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.heathkev.quizado.ui.MainNavigationFragment
import com.heathkev.quizado.R
import com.heathkev.quizado.data.QuestionsModel
import com.heathkev.quizado.databinding.FragmentSendQuestionBinding
import com.heathkev.quizado.utils.doOnApplyWindowInsets

class SendQuestionFragment : MainNavigationFragment() {

    private val viewModel: SendQuestionViewModel by lazy {
        ViewModelProvider(this).get(SendQuestionViewModel::class.java)
    }

    private lateinit var binding: FragmentSendQuestionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_send_question, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // snackbar for invalid question
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

        // all entries for the questions and submission
        val questionTitleEditable = binding.questionTitleLayout.editText!!.text
        val correctAnswerEditable = binding.questionCorrectAnswerLayout.editText!!.text
        val wrongAnswerEditable = binding.questionWrongAnswerLayout.editText!!.text
        val wrongAnswer2Editable = binding.questionWrongAnswer2Layout.editText!!.text
        val wrongAnswer3Editable = binding.questionWrongAnswer3Layout.editText!!.text
        binding.questionSubmitBtn.setOnClickListener {

            val question = QuestionsModel(
                "",
                questionTitleEditable.toString(),
                correctAnswerEditable.toString(),
                wrongAnswerEditable.toString(),
                wrongAnswer2Editable.toString(),
                wrongAnswer3Editable.toString(),
                correctAnswerEditable.toString(),
                0L
            )

            viewModel.onSubmitQuestion(question, binding.questionCategory.selectedItem.toString())
        }

        // display success submit
        viewModel.successSubmitEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()

                questionTitleEditable.clear()
                correctAnswerEditable.clear()
                wrongAnswerEditable.clear()
                wrongAnswer2Editable.clear()
                wrongAnswer3Editable.clear()
                correctAnswerEditable.clear()

                viewModel.doneSuccessfulSubmittingEvent()
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
}