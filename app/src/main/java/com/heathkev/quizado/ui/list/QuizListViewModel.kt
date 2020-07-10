package com.heathkev.quizado.ui.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.EventListener
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.firebase.FirebaseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "QuizListViewModel"
class QuizListViewModel : ViewModel() {

    private var firebaseRepository = FirebaseRepository()

    private val filter = FilterHolder()

    private var currentJob: Job? = null

    private val _navigateToSelectedQuizListModelPosition = MutableLiveData<QuizListModel>()
    val navigateToSelectedQuizListModelPosition: LiveData<QuizListModel>
        get() = _navigateToSelectedQuizListModelPosition

    private val _quizListModelListData = MutableLiveData<List<QuizListModel>>()
    val quizListModelData : LiveData<List<QuizListModel>>
        get() = _quizListModelListData

    private val _categoryList = MutableLiveData<List<String>>()
    val categoryList: LiveData<List<String>>
        get() = _categoryList

    private var isCategoryInitialized: Boolean = false;

    init {
        onFilterChanged("",true)
    }

    private fun getQuizList(filter: String?){
        val quizListQuery = if(filter.isNullOrEmpty()){
            firebaseRepository.getQuizList()
        }else{
            firebaseRepository.getQuizList().whereEqualTo("category", filter)
        }

        quizListQuery.addSnapshotListener(EventListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                _quizListModelListData.value = null
                return@EventListener
            }

            val quizListModelList: MutableList<QuizListModel> = mutableListOf()
            for (doc in value!!) {
                val quizItem = doc.toObject(QuizListModel::class.java)
                quizListModelList.add(quizItem)
            }
            _quizListModelListData.value = quizListModelList

            if(!isCategoryInitialized){
                _categoryList.value =  quizListModelList.map { it.category }.distinctBy { it }
                isCategoryInitialized =  true
            }
        })
    }

    private fun onQueryChanged() {
        currentJob?.cancel() // if a previous query is running cancel it before starting another
        currentJob = viewModelScope.launch {
            try {
                getQuizList(filter.currentValue)
            } catch (e: IOException) {
                _quizListModelListData.value = listOf()
                Log.d(TAG,"Error : ${e.message}")
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

    private class FilterHolder {
        var currentValue: String? = null
            private set

        fun update(changedFilter: String, isChecked: Boolean): Boolean {
            if (isChecked) {
                currentValue = changedFilter
                return true
            } else if (currentValue == changedFilter) {
                currentValue = null
                return true
            }
            return false
        }
    }
}