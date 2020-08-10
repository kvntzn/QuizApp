package com.heathkev.quizado.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.heathkev.quizado.MainNavigationFragment
import com.heathkev.quizado.R
import com.heathkev.quizado.data.User
import com.heathkev.quizado.databinding.FragmentLeadersBinding
import com.heathkev.quizado.databinding.FragmentProfileBinding
import com.heathkev.quizado.utils.doOnApplyWindowInsets

private lateinit var binding: FragmentProfileBinding

class ProfileFragment : MainNavigationFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = if(firebaseAuth.currentUser != null){
            val authUser = firebaseAuth.currentUser!!
            User(
                authUser.uid,
                authUser.displayName,
                authUser.photoUrl,
                authUser.email
            )
        }else{
            User()
        }

        val viewModelFactory = ProfileViewModelFactory(currentUser)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }
}