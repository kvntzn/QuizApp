package com.heathkev.quizado.ui.result

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.data.User

class ResultViewModelFactory(
    private val app: Application,
    private val currentUser: User,
    private val quizData: QuizListModel
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            return ResultViewModel(app, currentUser, quizData) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}