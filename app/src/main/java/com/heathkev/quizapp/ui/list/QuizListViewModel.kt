package com.heathkev.quizapp.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.heathkev.quizapp.data.QuizListModel
import com.heathkev.quizapp.firebase.FirebaseRepository

private const val TAG = "QuizListViewModel"
class QuizListViewModel : ViewModel() {

    private var firebaseRepository = FirebaseRepository()

    private val _navigateToSelectedQuizListModelPosition = MutableLiveData<QuizListModel>()
    val navigateToSelectedQuizListModelPosition: LiveData<QuizListModel>
        get() = _navigateToSelectedQuizListModelPosition

    val quizListModelData = firebaseRepository.getQuizList()

    fun displayQuizListModelDetails(quizListModel: QuizListModel) {
        _navigateToSelectedQuizListModelPosition.value = quizListModel
    }

    fun displayQuizListModelDetailsComplete() {
        _navigateToSelectedQuizListModelPosition.value = null
    }
}