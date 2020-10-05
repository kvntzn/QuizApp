package com.heathkev.quizado.ui.leaders

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.model.Leaderboard
import kotlinx.coroutines.*

private const val TAG = "LeadersViewModel"

class LeadersViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _first = MutableLiveData<Leaderboard>()
    val first: LiveData<Leaderboard>
        get() = _first

    private val _second = MutableLiveData<Leaderboard>()
    val second: LiveData<Leaderboard>
        get() = _second

    private val _third = MutableLiveData<Leaderboard>()
    val third: LiveData<Leaderboard>
        get() = _third

    private val _results = MutableLiveData<List<Leaderboard>>()
    val results: LiveData<List<Leaderboard>>
        get() = _results

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    init {
        initializeResults()
    }

    private fun initializeResults() {
        uiScope.launch {
            _isLoading.value = true
            val value = withContext(Dispatchers.IO) {
                firebaseRepository.getLeaderboards()
            }
            parseResults(value)

            _isLoading.value = false
        }
    }

    private fun parseResults(value: QuerySnapshot?) {
        val resultsList: MutableList<Leaderboard> = mutableListOf()
        for (doc in value!!) {
            val resultItem = doc.toObject<Leaderboard>()

            resultsList.add(resultItem)
        }

        _first.postValue(resultsList[0])
        _second.postValue(resultsList[1])
        _third.postValue(resultsList[2])

        _results.postValue(resultsList.drop(3))
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}