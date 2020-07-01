package com.heathkev.quizapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.heathkev.quizapp.R
import com.heathkev.quizapp.databinding.FragmentListBinding
import com.heathkev.quizapp.ui.list.QuizListAdapter
import com.heathkev.quizapp.ui.list.QuizListViewModel

/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list, container, false
        )

        val viewModel = ViewModelProvider(this).get(QuizListViewModel::class.java)
        binding.quizListViewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = QuizListAdapter(QuizListAdapter.OnClickListener {
            viewModel.displayQuizListModelDetails(it)
        })

        val listView = binding.listView
        listView.setHasFixedSize(true)
        listView.adapter = adapter

        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        val listProgress = binding.listProgress

        viewModel.quizListModelData.observe(viewLifecycleOwner, Observer {
            it?.let {
                listView.startAnimation(fadeInAnimation)
                listProgress.startAnimation(fadeOutAnimation)

                adapter.submitList(it)
            }
        })

        viewModel.navigateToSelectedQuizListModelPosition.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                this.findNavController().navigate(ListFragmentDirections.actionListFragmentToDetailFragment(it))
                viewModel.displayQuizListModelDetailsComplete()
            }
        })

        return binding.root
    }

}
