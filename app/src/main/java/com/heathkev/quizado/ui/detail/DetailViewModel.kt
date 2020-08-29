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
    private var firebaseRepository: FirebaseRepository,
    firebaseUser: FirebaseUserLiveData
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val currentUser: LiveData<User> = Transformations.map(firebaseUser){ user ->
        if(user != null){
            User(
                user.uid,
                user.displayName,
                user.photoUrl,
                user.email
            )
        }else{
            User()
        }
    }

    private val _quizListModel = MutableLiveData<QuizListModel>()
    val quiz: LiveData<QuizListModel>
        get() = _quizListModel

    private val _startQuizData = MutableLiveData<QuizListModel>()
    private val _resultPercentage = MutableLiveData<Long>()

    val startQuizData: LiveData<QuizListModel>
        get() = _startQuizData

    val resultPercentage: LiveData<Long>
        get() = _resultPercentage

    fun setQuizDetail(quizListModel: QuizListModel, user: User){
        uiScope.launch {
            getResult(quizListModel, user)
        }
    }

    fun startQuiz(quizListModel: QuizListModel) {
        _startQuizData.value = quizListModel
    }

    fun startQuizComplete() {
        _startQuizData.value = null
    }

    private suspend fun getResult(quizListModel: QuizListModel, user: User) {
        _quizListModel.value = quizListModel

        withContext(Dispatchers.IO) {
            val value =
                firebaseRepository.getResultsByQuizId(
                    quizListModel.quiz_id,
                    user.userId
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