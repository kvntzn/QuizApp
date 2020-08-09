package com.heathkev.quizado.ui.result

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.toObject
import com.heathkev.quizado.R
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.data.User
import com.heathkev.quizado.data.Result
import com.heathkev.quizado.firebase.FirebaseRepository
import kotlinx.coroutines.*

class ResultViewModel(
    private val app: Application,
    private val currentUser: User,
    val quizData: QuizListModel
) : AndroidViewModel(app) {

    private val firebaseRepository = FirebaseRepository()

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _score = MutableLiveData<String>()
    private val _scoreProgress = MutableLiveData<Int>()
    private val _result = MutableLiveData<Boolean>()
    private val _correctScore = MutableLiveData<Int>()
    private val _isLoading = MutableLiveData<Boolean>()

    val score: LiveData<String>
        get() = _score

    val scoreProgress: LiveData<Int>
        get() = _scoreProgress

    val result: LiveData<Boolean>
        get() = _result

    val correctScore: LiveData<Int>
        get() = _correctScore

    val isLoading: LiveData<Boolean>
        get() = _isLoading


    init {
        uiScope.launch {
            getResult()
        }
    }

    private suspend fun getResult() {
        withContext(Dispatchers.IO) {
            _isLoading.postValue(true)

            val value = firebaseRepository.getResultsByQuizIdAsync(quizData.quiz_id, currentUser.userId)

            val result = value?.toObject<Result>()

            if (result != null) {
                val correct = result.correct
                val wrong = result.wrong
                val missed = result.unanswered

                _correctScore.postValue(correct.toInt())
                val total = correct + wrong + missed

                val score = app.applicationContext.getString(R.string.score_over, correct, total)
                _score.postValue(score)

                val percent = (correct * 100) / total
                _scoreProgress.postValue(percent.toInt())

                val passed = correct > (total / 2)
                _result.postValue(passed)
            }

            _isLoading.postValue(false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}