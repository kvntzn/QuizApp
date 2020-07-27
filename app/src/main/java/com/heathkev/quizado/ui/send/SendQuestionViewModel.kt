package com.heathkev.quizado.ui.send

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.heathkev.quizado.data.QuestionsModel

class SendQuestionViewModel : ViewModel() {

    private var _showSnackbarEvent = MutableLiveData<Boolean?>()

    val showSnackBarEvent: LiveData<Boolean?>
        get() = _showSnackbarEvent

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = null
    }

    fun onSubmitQuestion(question: QuestionsModel, category: String) {
        val isChoicesAvailable = checkChoices(question.option_b, question.option_c, question.option_d)
        if(question.question.isNotEmpty() && question.answer.isNotEmpty() && isChoicesAvailable){
            //TODO: Submit Question to firebase
        }
        else{
            _showSnackbarEvent.value = true
        }
    }

    private fun checkChoices(option_a: String, option_b: String, option_c: String): Boolean {
        return option_a.isNotEmpty() ||option_b.isNotEmpty() || option_c.isNotEmpty()
    }

}