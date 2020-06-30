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

    private val _navigateToSelectedQuizListModel = MutableLiveData<QuizListModel>()
    val navigateToSelectedQuizListModel: LiveData<QuizListModel>
        get() = _navigateToSelectedQuizListModel

    private val firebaseRepository = FirebaseRepository(this)

    init {
        firebaseRepository.getQuizData()
    }

    fun displayQuizListModelDetails(quizListModel: QuizListModel) {
        _navigateToSelectedQuizListModel.value = quizListModel
    }

    fun displayQuizListModelDetailsComplete() {
        _navigateToSelectedQuizListModel.value = null
    }

    override fun quizListDataAdded(quizListModelsList: List<QuizListModel>) {
        _quizListModelData.value = quizListModelsList
    }

    override fun onError(e: Exception?) {
    }
}