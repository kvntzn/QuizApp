package com.heathkev.quizado.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.toObject
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.data.User
import com.heathkev.quizado.data.Result
import com.heathkev.quizado.firebase.FirebaseRepository
import kotlinx.coroutines.*

class DetailViewModel(private val quizListModel: QuizListModel, private val currentUser: User) :
    ViewModel() {

    private var firebaseRepository = FirebaseRepository()

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val quiz = quizListModel
    private val _startQuizData = MutableLiveData<QuizListModel>()
    private val _resultPercentage = MutableLiveData<Long>()

    val startQuizData: LiveData<QuizListModel>
        get() = _startQuizData

    val resultPercentage: LiveData<Long>
        get() = _resultPercentage

    init {
        uiScope.launch {
            getResult()
        }
    }

    fun startQuiz(quizListModel: QuizListModel) {
        _startQuizData.value = quizListModel
    }

    fun startQuizComplete() {
        _startQuizData.value = null
    }

    private suspend fun getResult() {
        withContext(Dispatchers.IO) {
            val value =
                firebaseRepository.getResultsByQuizId(
                    quizListModel.quiz_id,
                    currentUser.userId
                )

            val result = value?.toObject<Result>()

            // calculate progress
            if(result != null){
                val total = result.correct + result.wrong + result.unanswered
                val percent = (result.correct * 100) / total

                _resultPercentage.postValue(percent)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}