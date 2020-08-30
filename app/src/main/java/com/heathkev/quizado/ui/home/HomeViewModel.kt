package com.heathkev.quizado.ui.home

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.data.Result
import com.heathkev.quizado.data.User
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.firebase.FirebaseUserLiveData
import com.heathkev.quizado.ui.list.ListFragment
import com.heathkev.quizado.utils.Utility
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException

private const val TAG = "HomeViewModel"

class HomeViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _featuredQuiz = MutableLiveData<QuizListModel>()
    val featuredQuiz: LiveData<QuizListModel>
        get() = _featuredQuiz

    private val _quizList = MutableLiveData<List<QuizListModel>>()
    val quizList: LiveData<List<QuizListModel>>
        get() = _quizList

//    private val _categoryList = Utility.getCategoryResults().toMutableList()
//    private val _resultList = MutableLiveData<List<Result>>()
//    val resultList: LiveData<List<Result>>
//        get() = _resultList

    private val _navigateToQuizListModel = MutableLiveData<QuizListModel>()
    val navigateToQuizListModel: LiveData<QuizListModel>
        get() = _navigateToQuizListModel

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading


    init {
        fetchQuizList()
    }

    private fun fetchQuizList() {
        uiScope.launch {
            try {
                getQuizList()
            } catch (e: IOException) {
                _quizList.value = listOf()
                Timber.d("No quizzes retrieved")
            }
        }
    }

    private suspend fun getQuizList() {
        withContext(Dispatchers.IO) {
            _isLoading.postValue(true)
            parseQuizzes(firebaseRepository.getQuizList())

            _isLoading.postValue(false)
        }
    }

    private fun parseQuizzes(value: QuerySnapshot?) {
        val quizListModelList: MutableList<QuizListModel> = mutableListOf()
        for (doc in value!!) {
            val quizItem = doc.toObject<QuizListModel>()
            quizListModelList.add(quizItem)
        }

        _featuredQuiz.postValue(quizListModelList.first())
        _quizList.postValue(quizListModelList)
    }

//    private fun getResults() {
//        uiScope.launch {
//            _isLoading.value = true
//            val value = withContext(Dispatchers.IO) {
//                firebaseRepository.getResultsByUserId(user.uid)
//            }
//            _isLoading.value = false
//
//            parseResults(value)
//        }
//    }
//
//    private suspend fun parseResults(value: QuerySnapshot?) {
//        withContext(Dispatchers.Default) {
//            for (doc in value!!) {
//                val resultItem = doc.toObject<Result>()
//
//                val userResult = _categoryList.find { it.quiz_category == resultItem.quiz_category }
//                _categoryList.remove(userResult)
//                _categoryList.add(resultItem)
//            }
//
//            _resultList.postValue(_categoryList.sortedByDescending { it.correct })
//        }
//    }

    fun playQuiz() {
        _navigateToQuizListModel.value = featuredQuiz.value
    }

    fun playQuizComplete() {
        _navigateToQuizListModel.value = null
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}