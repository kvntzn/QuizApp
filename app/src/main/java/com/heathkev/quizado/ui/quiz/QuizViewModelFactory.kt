package com.heathkev.quizado.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.data.User

class QuizViewModelFactory(
    private val quizListModel: QuizListModel,
    private val currentUser: User
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            return QuizViewModel(quizListModel, currentUser) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

