package com.heathkev.quizapp.ui.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.heathkev.quizapp.data.QuizListModel
import com.heathkev.quizapp.firebase.FirebaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "QuizListViewModel"
class QuizListViewModel : ViewModel() {

    private var firebaseRepository = FirebaseRepository()

    private val viewModelJob = Job()
    private val scope = CoroutineScope(Dispatchers.Main + viewModelJob)


    private val _quizListModelData = MutableLiveData<List<QuizListModel>>()
    val quizListModelData: LiveData<List<QuizListModel>>
        get() = _quizListModelData

    private val _navigateToSelectedQuizListModelPosition = MutableLiveData<Int>()
    val navigateToSelectedQuizListModelPosition: LiveData<Int>
        get() = _navigateToSelectedQuizListModelPosition


    init {
        getQuizData()
    }

    private fun getQuizData() {
        scope.launch {
            firebaseRepository.getQuizData().addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    _quizListModelData.value = null
                    return@EventListener
                }

                val quizListModelList : MutableList<QuizListModel> = mutableListOf()
                for (doc in value!!) {
                    val addressItem = doc.toObject(QuizListModel::class.java)
                    quizListModelList.add(addressItem)
                }
                _quizListModelData.value = quizListModelList
            })
        }
    }

    fun displayQuizListModelDetails(position: Int) {
        _navigateToSelectedQuizListModelPosition.value = position
    }

    fun displayQuizListModelDetailsComplete() {
        _navigateToSelectedQuizListModelPosition.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}