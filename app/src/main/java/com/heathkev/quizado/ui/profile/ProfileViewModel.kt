package com.heathkev.quizado.ui.profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.toObject
import com.heathkev.quizado.model.Result
import com.heathkev.quizado.model.User
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.firebase.FirebaseUserLiveData
import com.heathkev.quizado.utils.Utility.Companion.getCategoryResults
import kotlinx.coroutines.*

private const val TAG = "ProfileViewModel"

class ProfileViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository,
    firebaseUser: FirebaseUserLiveData
) : ViewModel() {
    private val NO_RECORD: Int = 0

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _categoryList = getCategoryResults().toMutableList()

    private val _resultList = MutableLiveData<List<Result>>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _quizzesNumber = MutableLiveData<Int>()
    private val _passedNumber = MutableLiveData<Int>()
    private val _failedNumber = MutableLiveData<Int>()

    val user: LiveData<User> = Transformations.map(firebaseUser) { user ->
        if (user != null) {
            User(
                user.uid,
                user.displayName,
                user.photoUrl,
                user.email
            )
        } else {
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

    val resultList: LiveData<List<Result>>
        get() = _resultList

    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun getResult(currentUser: User) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.postValue(true)

                val value = firebaseRepository.getResultsByUserId(currentUser.userId)

                val resultsList: MutableList<Result> = mutableListOf()
                for (doc in value!!) {
                    val resultItem = doc.toObject<Result>()

                    resultsList.add(resultItem)

                    val userResult = _categoryList.find { it.quiz_category == resultItem.quiz_category }
                    _categoryList.remove(userResult)
                    _categoryList.add(resultItem)
                }

                _resultList.postValue(_categoryList.sortedByDescending { it.correct })
                countQuizResult(resultsList)

                _isLoading.postValue(false)
            }
        }
    }

    private fun countQuizResult(results: List<Result>) {
        var passed = 0
        var failed = 0

        for (i in results) if (i.correct > i.wrong) passed++ else failed++

        _passedNumber.postValue(passed)
        _failedNumber.postValue(failed)
        _quizzesNumber.postValue(results.count())
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