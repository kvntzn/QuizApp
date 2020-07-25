package com.heathkev.quizado.ui.result

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.heathkev.quizado.R
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.data.User
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

    val score: LiveData<String>
        get() = _score

    val scoreProgress: LiveData<Int>
        get() = _scoreProgress

    val result: LiveData<Boolean>
        get() = _result

    val correctScore: LiveData<Int>
        get() = _correctScore


    init {
        uiScope.launch {
            getResult()
        }
    }

    private suspend fun getResult() {
        withContext(Dispatchers.IO) {
            firebaseRepository.getResultsByQuizId(quizData.quiz_id).document(currentUser.userId)
                .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result

                    if (result != null) {
                        val correct = result.getLong("correct")!!
                        val wrong = result.getLong("wrong")!!
                        val missed = result.getLong("unanswered")!!

                        _correctScore.value = correct.toInt()
                        val total = correct + wrong + missed
                        _score.value =
                            app.applicationContext.getString(R.string.score_over, correct, total)

                        val percent = (correct * 100) / total
                        _scoreProgress.value = percent.toInt()

                        val passed = correct > (total / 2)
                        _result.value = passed

                    }
                }
            }
        }
    }
}