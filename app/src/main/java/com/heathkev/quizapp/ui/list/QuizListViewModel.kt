package com.heathkev.quizapp.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.heathkev.quizapp.data.QuizListModel
import com.heathkev.quizapp.firebase.FirebaseRepository

class QuizListViewModel : ViewModel(), FirebaseRepository.OnFireStoreTaskComplete {

    private val _quizListModelData = MutableLiveData<List<QuizListModel>>()
    val quizListModelData: LiveData<List<QuizListModel>>
        get() = _quizListModelData

    private val _navigateToSelectedQuizListModelPosition = MutableLiveData<Int>()
    val navigateToSelectedQuizListModelPosition: LiveData<Int>
        get() = _navigateToSelectedQuizListModelPosition

    private val firebaseRepository = FirebaseRepository(this)

    init {
        firebaseRepository.getQuizData()
    }

    fun displayQuizListModelDetails(position: Int) {
        _navigateToSelectedQuizListModelPosition.value = position
    }

    fun displayQuizListModelDetailsComplete() {
        _navigateToSelectedQuizListModelPosition.value = null
    }

    override fun quizListDataAdded(quizListModelsList: List<QuizListModel>) {
        _quizListModelData.value = quizListModelsList
    }

    override fun onError(e: Exception?) {
    }
}