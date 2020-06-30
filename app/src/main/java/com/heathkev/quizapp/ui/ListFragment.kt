package com.heathkev.quizapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
            inflater, R.layout.fragment_list, container, false)

        val quizListViewModel =  ViewModelProvider(this).get(QuizListViewModel::class.java)
        binding.quizListViewModel = quizListViewModel
        binding.lifecycleOwner = this

        val adapter = QuizListAdapter()

        val listView = binding.listView
        listView.layoutManager = LinearLayoutManager(activity)
        listView.setHasFixedSize(true)
        listView.adapter = adapter

        quizListViewModel.quizListModelData.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.quizListModels = it
                adapter.notifyDataSetChanged()
            }
        })

        return binding.root
    }

}
