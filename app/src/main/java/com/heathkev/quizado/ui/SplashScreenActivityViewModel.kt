package com.heathkev.quizado.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth
import com.heathkev.quizado.result.Event

class SplashScreenActivityViewModel @ViewModelInject constructor(
) : ViewModel() {

    val launchDestination = liveData {
        val firebaseUser= FirebaseAuth.getInstance()
            if (firebaseUser.currentUser != null) {
                emit(Event(LaunchDestination.MAIN_ACTIVITY))
            } else {
                emit(Event(LaunchDestination.LOGIN))
            }
    }
}

enum class LaunchDestination {
    LOGIN,
    MAIN_ACTIVITY
}