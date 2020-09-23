package com.heathkev.quizado.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.heathkev.quizado.model.QuizListModel
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.firebase.FirebaseUserLiveData
import com.heathkev.quizado.model.User
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import kotlin.random.Random

private const val TAG = "HomeViewModel"

class HomeViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository,
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

    private val _featuredQuiz = MutableLiveData<QuizListModel>()
    val featuredQuiz: LiveData<QuizListModel>
        get() = _featuredQuiz

    private val _recommendedQuizList = MutableLiveData<List<QuizListModel>>()
    val recommendedQuizList: LiveData<List<QuizListModel>>
        get() = _recommendedQuizList

    private val _popularQuizList = MutableLiveData<List<QuizListModel>>()
    val popularQuizList: LiveData<List<QuizListModel>>
        get() = _popularQuizList

    private val _navigateToQuizListModel = MutableLiveData<QuizListModel>()
    val navigateToQuizListModel: LiveData<QuizListModel>
        get() = _navigateToQuizListModel

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun fetchQuizList(userId: String) {
        uiScope.launch {
            try {
                getQuizList(userId)
            } catch (e: IOException) {
                _recommendedQuizList.value = listOf()
                _popularQuizList.value = listOf()

                Timber.d("No quizzes retrieved")
            }
        }
    }

    private suspend fun getQuizList(userId: String) {
        withContext(Dispatchers.IO) {
            _isLoading.postValue(true)

            val recommendedQuizzes = parseQuizzes(firebaseRepository.getRecommendedQuiz(userId))
            val popularQuizzes = parseQuizzes(firebaseRepository.getMostPopularQuiz(userId))

            _recommendedQuizList.postValue(recommendedQuizzes)
            _popularQuizList.postValue(popularQuizzes)

            _featuredQuiz.postValue(popularQuizzes[1])

            _isLoading.postValue(false)
        }
    }

    private fun parseQuizzes(value: QuerySnapshot?) : MutableList<QuizListModel> {
        val quizListModelList: MutableList<QuizListModel> = mutableListOf()
        for (doc in value!!) {
            val quizItem = doc.toObject<QuizListModel>()
            quizListModelList.add(quizItem)
        }

        Timber.d("$quizListModelList")
        return quizListModelList;
    }

    fun playQuiz() {
        _navigateToQuizListModel.value = featuredQuiz.value
    }

    fun playQuizComplete() {
        _navigateToQuizListModel.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}