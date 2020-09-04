package com.heathkev.quizado.ui.signin

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import com.heathkev.quizado.model.User
import com.heathkev.quizado.firebase.FirebaseUserLiveData
import com.heathkev.quizado.result.Event

class SignOutViewModel @ViewModelInject constructor(
    private val firebaseUserLiveData: FirebaseUserLiveData
) : ViewModel(){

    val currentUser: LiveData<User> = map(firebaseUserLiveData){ user ->
        if(user != null){
            User(
                user.uid,
                user.displayName,
                user.photoUrl,
                user.email
            )
        }else{
            User()
        }
    }

    private val _onSignOutAction = MutableLiveData<Event<Unit>>()
    val onSignOutAction: LiveData<Event<Unit>>
        get() = _onSignOutAction

    fun onSignOut(){
        _onSignOutAction.value = Event(Unit)
    }
}