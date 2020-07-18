package com.heathkev.quizado.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.heathkev.quizado.data.User

class ProfileViewModel(currentUser: User) : ViewModel(){

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    init {
        _user.value = currentUser
    }
}