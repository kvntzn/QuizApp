package com.heathkev.quizado.ui.start

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.firebase.FirebaseUserLiveData

class LoginViewModel : ViewModel() {

    private var firebaseRepository = FirebaseRepository()
    private val currentUser = FirebaseUserLiveData()

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState: LiveData<AuthenticationState> = map(currentUser) { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    fun registerUser() {
        currentUser.value?.let {
            val userMap = HashMap<String, Any?>()
            userMap["name"] = it.displayName
            userMap["email"] = it.email
            userMap["image"] = if (Uri.EMPTY != it.photoUrl) it.photoUrl.toString() else it.photoUrl
            firebaseRepository.getUsers().document(it.uid).set(userMap)
        }
    }
}
