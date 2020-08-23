package com.heathkev.quizado.ui.profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.toObject
import com.heathkev.quizado.data.Result
import com.heathkev.quizado.data.User
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.firebase.FirebaseUserLiveData
import kotlinx.coroutines.*

private const val TAG = "ProfileViewModel"
class ProfileViewModel @ViewModelInject constructor (
    private val firebaseRepository: FirebaseRepository,
    private val firebaseUser: FirebaseUserLiveData
) : ViewModel() {
    private val NO_RECORD: Int = 0

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _quizzesNumber = MutableLiveData<Int>()
    private val _passedNumber = MutableLiveData<Int>()
    private val _failedNumber = MutableLiveData<Int>()

    val user: LiveData<User> = Transformations.map(firebaseUser){ user ->
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

    val quizzesNumber: LiveData<String> = Transformations.map(_quizzesNumber) {
        it.toString()
    }

    val passedNumber: LiveData<String> = Transformations.map(_passedNumber) {
        it.toString()
    }

    val failedNumber: LiveData<String> = Transformations.map(_failedNumber) {
        it.toString()
    }

    fun getResult(currentUser: User) {
        uiScope.launch {
            val value = withContext(Dispatchers.IO) {
                firebaseRepository.getResultsByUserId(currentUser.userId)
            }

            val resultsList: MutableList<Result> = mutableListOf()
            for (doc in value!!) {
                val resultItem = doc.toObject<Result>()

                resultsList.add(resultItem)
            }

            countQuizResult(resultsList)
        }
    }

    private fun countQuizResult(results: List<Result>) {
        var passed = 0
        var failed = 0

        for (i in results) if (i.correct > i.wrong) passed++ else failed++

        _passedNumber.value = passed
        _failedNumber.value = failed
        _quizzesNumber.value = results.count()

    }

    private fun setToZero() {
        _quizzesNumber.value = NO_RECORD
        _passedNumber.value = NO_RECORD
        _failedNumber.value = NO_RECORD
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}