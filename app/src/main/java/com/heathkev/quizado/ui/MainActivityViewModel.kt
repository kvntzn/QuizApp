package com.heathkev.quizado.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.heathkev.quizado.firebase.FirebaseUserLiveData
import com.heathkev.quizado.result.Event
import com.heathkev.quizado.ui.theme.ThemedActivityDelegate

class MainActivityViewModel @ViewModelInject constructor(
    firebaseUserLiveData: FirebaseUserLiveData,
    themedActivityDelegate: ThemedActivityDelegate
) : ViewModel(),
    ThemedActivityDelegate by themedActivityDelegate {

    val currentUser = firebaseUserLiveData

    private val _navigateToSignOutDialogAction = MutableLiveData<Event<Unit>>()
    val navigateToSignOutDialogAction: LiveData<Event<Unit>>
        get() = _navigateToSignOutDialogAction

    fun onProfileClicked(){
        _navigateToSignOutDialogAction.value = Event(Unit)
    }
}