package com.heathkev.quizapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.heathkev.quizapp.R
import com.heathkev.quizapp.databinding.FragmentStartBinding
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

        (activity as AppCompatActivity).supportActionBar?.hide()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val startFeedBackText = start_feedback
        startFeedBackText.text = getString(R.string.checking_user_account)
    }

    override fun onStart() {
        super.onStart()
        val startFeedBackText = start_feedback

        val currentUser = firebaseAuth.currentUser

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.startFragment, true)
            .build()
        if(currentUser == null){

            startFeedBackText.text = getString(R.string.create_account)

            // Create new account
            firebaseAuth.signInAnonymously().addOnCompleteListener {
                if(it.isSuccessful){
                    startFeedBackText.text = getString(R.string.account_created)

                    requireView().findNavController().navigate(StartFragmentDirections.actionStartFragmentToListFragment(),navOptions)
                }else{
                    Log.d(START_TAG, "Start log: ${it.exception}")
                }
            }
        } else{
            // Navigate to Homepage
            startFeedBackText.text = getString(R.string.logged_in)
            requireView().findNavController().navigate(StartFragmentDirections.actionStartFragmentToListFragment(), navOptions)
        }
    }
}
