package com.heathkev.quizado.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import com.heathkev.quizado.firebase.FirebaseUserLiveData

class SplashScreenActivityViewModel @ViewModelInject constructor(
    firebaseUser: FirebaseUserLiveData
) : ViewModel() {

    val launchDestination: LiveData<LaunchDestination> = map(firebaseUser){
        if (it != null) {
            LaunchDestination.MAIN_ACTIVITY
        } else {
            LaunchDestination.LOGIN
        }
    }
}

enum class LaunchDestination {
    LOGIN,
    MAIN_ACTIVITY
}