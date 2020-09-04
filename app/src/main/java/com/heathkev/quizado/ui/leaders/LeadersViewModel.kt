package com.heathkev.quizado.ui.leaders

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.heathkev.quizado.model.Result
import com.heathkev.quizado.firebase.FirebaseRepository
import kotlinx.coroutines.*
import timber.log.Timber

private const val TAG = "LeadersViewModel"

class LeadersViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _first = MutableLiveData<Result>()
    val first: LiveData<Result>
        get() = _first

    private val _second = MutableLiveData<Result>()
    val second: LiveData<Result>
        get() = _second

    private val _third = MutableLiveData<Result>()
    val third: LiveData<Result>
        get() = _third

    private val _results = MutableLiveData<List<Result>>()
    val results: LiveData<List<Result>>
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
                firebaseRepository.getAllResults()
            }
            groupResults(value)

            _isLoading.value = false
        }
    }

    private fun groupResults(value: QuerySnapshot?) {
        val resultsList: MutableList<Result> = mutableListOf()
        for (doc in value!!) {
            val resultItem = doc.toObject<Result>()

            resultsList.add(resultItem)
        }

        val grouped =
            resultsList.groupingBy(Result::user_id).aggregate { _, acc: Result?, e, _ ->
                Result(
                    e.user_id,
                    e.player_name,
                    e.player_photo,
                    "",
                    "",
                    (acc?.correct ?: 0) + e.correct,
                    e.unanswered,
                    e.wrong
                )
            }

        Timber.d("Results Grouped:$grouped")

        val results: MutableList<Result> = grouped.values.take(10).sortedByDescending { it.correct }.toMutableList()
        _first.postValue(results[0])
        _second.postValue(results[1])
        _third.postValue(results[2])

        _results.postValue(results.drop(3))
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}