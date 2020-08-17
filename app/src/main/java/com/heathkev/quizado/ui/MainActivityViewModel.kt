package com.heathkev.quizado.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.firebase.FirebaseUserLiveData

class MainActivityViewModel @ViewModelInject constructor(
    private val firebaseUser: FirebaseUserLiveData
) : ViewModel() {
    val currentUser = firebaseUser
}