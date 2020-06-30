package com.heathkev.quizapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import com.heathkev.quizapp.R
import com.heathkev.quizapp.databinding.FragmentStartBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_start.*

/**
 * A simple [Fragment] subclass.
 */
private const val START_TAG = "START_LOG";
class StartFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentStartBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_start, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val progressBar = start_progress
        val startFeedBackText = start_feedback
        startFeedBackText.text = getString(R.string.checking_user_account)
    }

    override fun onStart() {
        super.onStart()
        val startFeedBackText = start_feedback

        val currentUser = firebaseAuth.currentUser
        if(currentUser == null){

            startFeedBackText.setText("Creating Account...")

            // Create new account
            firebaseAuth.signInAnonymously().addOnCompleteListener {
                if(it.isSuccessful){
                    startFeedBackText.setText("Account Created...")
                    requireView().findNavController().navigate(StartFragmentDirections.actionStartFragmentToListFragment())
                }else{
                    Log.d(START_TAG, "Start log: ${it.exception}")
                }
            }
        } else{
            // Navigate to Homepage
            startFeedBackText.setText("Logged in...")
            requireView().findNavController().navigate(StartFragmentDirections.actionStartFragmentToListFragment())
        }
    }
}
