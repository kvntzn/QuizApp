package com.heathkev.quizado.ui.leaders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.heathkev.quizado.data.Result
import com.heathkev.quizado.firebase.FirebaseRepository
import kotlinx.coroutines.*

private const val TAG = "LeadersViewModel"
class LeadersViewModel : ViewModel(){

    private val firebaseRepository = FirebaseRepository()

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _results = MutableLiveData<List<Result>>()
    val results : LiveData<List<Result>>
        get() = _results

    init{
        initializeResults()
    }

    private fun initializeResults() {
        uiScope.launch {
            getResults()
        }
    }

    private suspend fun getResults(){
        withContext(Dispatchers.IO) {
            firebaseRepository.getAllResults().addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@EventListener
                }

                val resultsList: MutableList<Result> = mutableListOf()
                for (doc in value!!) {
                    val resultItem = doc.toObject(Result::class.java)

                    resultsList.add(resultItem)
                }

                groupResults(resultsList)
            })
        }
    }

    private fun groupResults(list: MutableList<Result>){

        val grouped = list.groupingBy(Result::user_id).aggregate { _, acc: Result?, e, _ ->
            Result(
                e.user_id,
                e.player_name,
                e.player_photo,
                (acc?.correct ?: 0) + e.correct,
                e.unanswered,
                e.wrong)
        }

        Log.d(TAG, "Results Grouped:$grouped")
        _results.value = grouped.values.toList().sortedByDescending { it.correct }
    }

}