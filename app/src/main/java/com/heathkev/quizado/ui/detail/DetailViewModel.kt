package com.heathkev.quizado.ui.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.toObject
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.data.User
import com.heathkev.quizado.data.Result
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.firebase.FirebaseUserLiveData
import kotlinx.coroutines.*

class DetailViewModel @ViewModelInject constructor (
    private var firebaseRepository: FirebaseRepository
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _quizListModel = MutableLiveData<QuizListModel>()
    val quiz: LiveData<QuizListModel>
        get() = _quizListModel

    private val _currentUser = MutableLiveData<User>()

    private val _startQuizData = MutableLiveData<QuizListModel>()
    private val _resultPercentage = MutableLiveData<Long>()

    val startQuizData: LiveData<QuizListModel>
        get() = _startQuizData

    val resultPercentage: LiveData<Long>
        get() = _resultPercentage

    fun setQuizDetail(quizListModel: QuizListModel, currentUser: User){
        _quizListModel.value = quizListModel
        _currentUser.value = currentUser

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
                    _quizListModel.value!!.quiz_id,
                    _currentUser.value!!.userId
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