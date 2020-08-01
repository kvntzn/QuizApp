package com.heathkev.quizado.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.data.Result
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.utils.Utility
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

private const val TAG = "HomeViewModel"
class HomeViewModel : ViewModel(){

    private val firebaseRepository = FirebaseRepository()
    val user = FirebaseAuth.getInstance().currentUser!!

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _categoryList = Utility.getCategoryResults().toMutableList()
    private val _resultList = MutableLiveData<List<Result>>()
    val resultList: LiveData<List<Result>>
        get() = _resultList

    private val _navigateToQuizListModel = MutableLiveData<QuizListModel>()
    val navigateToQuizListModel: LiveData<QuizListModel>
        get() = _navigateToQuizListModel

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
            val value = firebaseRepository.getResultsByUserId(user.uid).get().await()

                for (doc in value!!) {
                    val resultItem = doc.toObject(Result::class.java)

                    val userResult = _categoryList.find { it.quiz_category == resultItem.quiz_category }
                    _categoryList.remove(userResult)
                    _categoryList.add(resultItem)
                }

                _resultList.postValue(_categoryList.sortedByDescending { it.correct })
        }
    }

    fun playQuiz(){
        uiScope.launch {
            assignQuiz()
        }
    }

    private suspend fun assignQuiz(){
        withContext(Dispatchers.IO){
            firebaseRepository.getQuizList().limit(1).addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@EventListener
                }

                for (doc in value!!) {
                    val quizItem = doc.toObject(QuizListModel::class.java)
                    _navigateToQuizListModel.value = quizItem
                }
            })
        }
    }

    fun playQuizComplete() {
        _navigateToQuizListModel.value = null
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}