package com.heathkev.quizado.ui.list

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.heathkev.quizado.model.QuizListModel
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.ui.list.ListFragment.Companion.DEFAULT_CATEGORY
import com.heathkev.quizado.utils.Utility.Companion.Categories
import kotlinx.coroutines.*
import java.io.IOException

private const val TAG = "QuizListViewModel"

class QuizListViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val filter = FilterHolder()

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _navigateToSelectedQuizListModelPosition = MutableLiveData<QuizListModel>()
    val navigateToSelectedQuizListModelPosition: LiveData<QuizListModel>
        get() = _navigateToSelectedQuizListModelPosition

    private val _allQuizList = MutableLiveData<List<QuizListModel>>()

    private val _quizList = MutableLiveData<List<QuizListModel>>()
    val quizList: LiveData<List<QuizListModel>>
        get() = _quizList

    private val _categoryList = MutableLiveData<List<String>>()
    val categoryList: LiveData<List<String>>
        get() = _categoryList

    private val _category = MutableLiveData<String>()
    val category: LiveData<String>
        get() = _category

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private var isCategoryInitialized: Boolean = false;

    init {
        onQueryChanged()
    }

    private suspend fun getQuizList(filter: String) {
        withContext(Dispatchers.IO) {
            if (filter == DEFAULT_CATEGORY) {
                if (_allQuizList.value == null) {
                    _isLoading.postValue(true)

                    parseQuizzes(firebaseRepository.getQuizList())

                    _isLoading.postValue(false)
                } else {
                    _quizList.postValue(_allQuizList.value)
                }
            } else {
                val filteredQuizzes = _allQuizList.value?.filter { it.category == filter }
                _quizList.postValue(filteredQuizzes)
            }
        }
    }

    private fun parseQuizzes(value: QuerySnapshot?) {
        val quizListModelList: MutableList<QuizListModel> = mutableListOf()
        for (doc in value!!) {
            val quizItem = doc.toObject<QuizListModel>()
            quizListModelList.add(quizItem)
        }

        _allQuizList.postValue(quizListModelList)
        _quizList.postValue(quizListModelList)

        if (!isCategoryInitialized) {
            _categoryList.postValue(Categories.values().map { it.toString() }.toList())
            isCategoryInitialized = true
        }
    }

    private fun onQueryChanged() {
        uiScope.launch {
            try {
                _category.value = filter.currentValue
                getQuizList(filter.currentValue)
            } catch (e: IOException) {
                _category.value = ""
                _quizList.value = listOf()
                Log.d(TAG, "Error : ${e.message}")
            }
        }
    }

    fun onFilterChanged(filter: String, isChecked: Boolean) {
        if (this.filter.update(filter, isChecked)) {
            onQueryChanged()
        }
    }

    fun displayQuizListModelDetails(quizListModel: QuizListModel) {
        _navigateToSelectedQuizListModelPosition.value = quizListModel
    }

    fun displayQuizListModelDetailsComplete() {
        _navigateToSelectedQuizListModelPosition.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    class FilterHolder {
        var currentValue: String = DEFAULT_CATEGORY
            private set

        fun update(changedFilter: String, isChecked: Boolean): Boolean {
            if (isChecked) {
                currentValue = changedFilter
                return true
            } else if (currentValue == changedFilter) {
                currentValue = DEFAULT_CATEGORY
                return true
            }
            return false
        }
    }
}