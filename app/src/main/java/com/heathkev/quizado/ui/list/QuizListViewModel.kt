package com.heathkev.quizado.ui.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.firebase.FirebaseRepository
import com.heathkev.quizado.ui.list.ListFragment.Companion.DEFAULT_CATEGORY
import com.heathkev.quizado.utils.Utility.Companion.Categories
import kotlinx.coroutines.*
import java.io.IOException

private const val TAG = "QuizListViewModel"
class QuizListViewModel : ViewModel() {

    private var firebaseRepository = FirebaseRepository()

    private val filter = FilterHolder()

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _navigateToSelectedQuizListModelPosition = MutableLiveData<QuizListModel>()
    val navigateToSelectedQuizListModelPosition: LiveData<QuizListModel>
        get() = _navigateToSelectedQuizListModelPosition

    private val _quizListModelListData = MutableLiveData<List<QuizListModel>>()
    val quizListModelData : LiveData<List<QuizListModel>>
        get() = _quizListModelListData

    private val _categoryList = MutableLiveData<List<String>>()
    val categoryList: LiveData<List<String>>
        get() = _categoryList

    private val _category = MutableLiveData<String>()
    val category: LiveData<String>
        get() = _category

    private var isCategoryInitialized: Boolean = false;

    init {
        onQueryChanged()
    }

    private suspend fun getQuizList(filter: String) {
       val value = withContext(Dispatchers.IO){
            if (filter == DEFAULT_CATEGORY) firebaseRepository.getQuizListAsync() else firebaseRepository.getQuizListAsync(filter)
        }

        parseQuizzes(value)
    }

    private fun parseQuizzes(value: QuerySnapshot?) {
        val quizListModelList: MutableList<QuizListModel> = mutableListOf()
        for (doc in value!!) {
            val quizItem = doc.toObject<QuizListModel>()
            quizListModelList.add(quizItem)
        }
        _quizListModelListData.value = quizListModelList

        if (!isCategoryInitialized) {
            _categoryList.value = Categories.values().map { it.toString() }.toList()
            isCategoryInitialized = true
        }
    }

    private fun onQueryChanged() {
        uiScope.launch {
            try {
                getQuizList(filter.currentValue)
                _category.value = filter.currentValue
            } catch (e: IOException) {
                _quizListModelListData.value = listOf()
                _category.value = ""
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