package com.heathkev.quizapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.heathkev.quizapp.R
import com.heathkev.quizapp.databinding.FragmentDetailBinding
import com.heathkev.quizapp.ui.list.QuizListViewModel

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentDetailBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_detail, container, false)

        val viewModel = ViewModelProvider(this).get(QuizListViewModel::class.java)

        val arguments =
            DetailFragmentArgs.fromBundle(
                requireArguments()
            )
        val position = arguments.selectedQuizListModelPosition

        viewModel.quizListModelData.observe(viewLifecycleOwner, Observer {
            binding.quizListModel = it[position]
        })

        binding.detailsStartBtn.setOnClickListener{
            viewModel.displayQuizListModelDetails(position)
        }

        viewModel.navigateToSelectedQuizListModelPosition.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                this.findNavController().navigate(
                    DetailFragmentDirections.actionDetailFragmentToQuizFragment(
                        it,
                        binding.quizListModel!!.quiz_id
                    )
                )
                viewModel.displayQuizListModelDetailsComplete()
            }
        })

        return binding.root
    }

}
