//package com.heathkev.quizado.ui.detail
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.heathkev.quizado.model.QuizListModel
//import com.heathkev.quizado.model.User
//import com.heathkev.quizado.ui.quiz.QuizViewModel
//
//
//class DetailViewModelFactory(
//    private val quizListModel: QuizListModel,
//    private val currentUser: User
//) : ViewModelProvider.Factory {
//    @Suppress("unchecked_cast")
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
//            return DetailViewModel(quizListModel, currentUser) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
//
