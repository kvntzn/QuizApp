package com.heathkev.quizado.ui.leaders

import android.util.Log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.heathkev.quizado.data.Result

private const val TAG = "LeadersViewModel"
class LeadersViewModel : ViewModel(){

    private val _results = MutableLiveData<List<Result>>()
    val results : LiveData<List<Result>>
        get() = _results

    init{
        getResults()
    }

    fun getResults(){
        FirebaseFirestore.getInstance().collectionGroup("Results").addSnapshotListener(EventListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@EventListener
            }

            val resultsList: MutableList<Result> = mutableListOf()
            for (doc in value!!) {
                val resultItem = doc.toObject(Result::class.java)

                resultsList.add(resultItem)
                Log.w(TAG, "Result $resultItem")
            }

            groupResults(resultsList)
        })
    }

    fun groupResults(list: MutableList<Result>){

        val grouped = list.groupingBy(Result::user_id).aggregate { it, acc: Result?, e, _ ->
            Result(
                e.user_id,
                (acc?.correct ?: 0) + it.sumBy { e.correct.toInt() }.toLong(),
                (acc?.wrong ?: 0) + it.sumBy { e.wrong.toInt() }.toLong(),
                (acc?.unanswered ?: 0) + it.sumBy { e.unanswered.toInt() }.toLong())
        }.toList()

        Log.d(TAG, grouped.toString())
//        _results.value =
    }

}