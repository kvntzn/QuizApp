package com.heathkev.quizado.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.heathkev.quizado.model.QuizListModel
import com.heathkev.quizado.firebase.FirebaseRepository
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

    // TODO: update queries to get recommended and trending
    private val _quizList = MutableLiveData<List<QuizListModel>>()
    val quizList: LiveData<List<QuizListModel>>
        get() = _quizList

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