package com.heathkev.quizapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.heathkev.quizapp.R

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val arguments = DetailFragmentArgs.fromBundle(requireArguments())

        Log.d("DetailFragment", arguments.toString())
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

}
