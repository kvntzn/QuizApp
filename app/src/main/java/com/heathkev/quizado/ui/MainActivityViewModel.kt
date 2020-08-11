package com.heathkev.quizado.ui

import androidx.lifecycle.ViewModel
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.firebase.FirebaseUserLiveData

class MainActivityViewModel : ViewModel(){
    private var firebaseRepository = FirebaseRepository()
    val currentUser = FirebaseUserLiveData()
}