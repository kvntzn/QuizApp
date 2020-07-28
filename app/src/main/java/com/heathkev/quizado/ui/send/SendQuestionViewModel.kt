package com.heathkev.quizado.ui.send

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.heathkev.quizado.data.QuestionsModel
import com.heathkev.quizado.firebase.FirebaseRepository
import kotlinx.coroutines.*

private const val TAG = "SendQuestionViewModel"
class SendQuestionViewModel : ViewModel() {

    private val firebaseRepository = FirebaseRepository()

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _showSnackbarEvent = MutableLiveData<Boolean?>()
    private val _successSubmitEvent = MutableLiveData<Boolean?>()
    private val _question = MutableLiveData<QuestionsModel>()
    private val _category = MutableLiveData<String>()

    val category: LiveData<String>
        get() = _category

    val question: LiveData<QuestionsModel>
            get() = _question

    val successSubmitEvent: LiveData<Boolean?>
        get() = _successSubmitEvent

    val showSnackBarEvent: LiveData<Boolean?>
        get() = _showSnackbarEvent

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = null
    }

    fun doneSuccessfulSubmittingEvent() {
        _successSubmitEvent.value = null
    }


    fun onSubmitQuestion(question: QuestionsModel, category: String) {
        uiScope.launch {
            val isChoicesAvailable = checkChoices(question.option_b, question.option_c, question.option_d)

            if(question.question.isNotEmpty() && question.answer.isNotEmpty() && isChoicesAvailable){

                val questionMap = HashMap<String, Any?>()
                questionMap["answer"] = question.answer
                questionMap["question"] = question.question

                questionMap["option_a"] = question.option_a
                questionMap["option_b"] = question.option_b
                questionMap["option_c"] = question.option_c
                questionMap["option_d"] = question.option_d

                questionMap["category"] = category

                submit(questionMap)
            }
            else{
                _showSnackbarEvent.value = true
            }
        }
    }

    private suspend fun submit(questionMap: HashMap<String, Any?>) {
        withContext(Dispatchers.IO){
            firebaseRepository.getQuestionRequest().document().set(questionMap).addOnCompleteListener {
                if (it.isSuccessful) {
                    //SUCCESS
                    _successSubmitEvent.value = true
                    Log.d(TAG, "Successful Submit")
                } else {
                    // ERROR Occurred
                    _successSubmitEvent.value = false
                    Log.d(TAG, "Something went wrong ${it.exception}")
                }
            }
        }
    }



    private fun checkChoices(option_a: String, option_b: String, option_c: String): Boolean {
        return option_a.isNotEmpty() ||option_b.isNotEmpty() || option_c.isNotEmpty()
    }

}