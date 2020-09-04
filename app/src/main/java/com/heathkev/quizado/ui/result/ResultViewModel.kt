package com.heathkev.quizado.ui.result

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.toObject
import com.heathkev.quizado.model.QuizListModel
import com.heathkev.quizado.model.Result
import com.heathkev.quizado.model.User
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.firebase.FirebaseUserLiveData
import kotlinx.coroutines.*

class ResultViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository,
    firebaseUser: FirebaseUserLiveData
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _scoreProgress = MutableLiveData<Int>()
    private val _result = MutableLiveData<Boolean>()
    private val _correctScore = MutableLiveData<Int>()
    private val _isLoading = MutableLiveData<Boolean>()

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

    val scoreProgress: LiveData<Int>
        get() = _scoreProgress

    val result: LiveData<Boolean>
        get() = _result

    val correctScore: LiveData<Int>
        get() = _correctScore

    val isLoading: LiveData<Boolean>
        get() = _isLoading


    fun fetchQuizResult(quizListModel: QuizListModel, user: User){
        uiScope.launch {
            _isLoading.value = true
            getResult(quizListModel, user)
            _isLoading.value = false
        }
    }

    private suspend fun getResult(quizListModel: QuizListModel, user: User)  {
        withContext(Dispatchers.IO) {

            val value = firebaseRepository.getResultsByQuizId(quizListModel.quiz_id, user.userId)

            val result = value?.toObject<Result>()

            if (result != null) {
                val correct = result.correct
                val wrong = result.wrong
                val missed = result.unanswered

                _correctScore.postValue(correct.toInt())
                val total = correct + wrong + missed

                val percent = (correct * 100) / total
                _scoreProgress.postValue(percent.toInt())

                val passed = correct > (total / 2)
                _result.postValue(passed)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}