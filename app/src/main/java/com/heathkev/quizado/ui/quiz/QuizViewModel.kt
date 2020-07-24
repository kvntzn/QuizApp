package com.heathkev.quizado.ui.quiz

import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.heathkev.quizado.data.QuestionsModel
import com.heathkev.quizado.data.QuizListModel
import com.heathkev.quizado.data.User
import com.heathkev.quizado.firebase.FirebaseRepository
import kotlinx.coroutines.*

private const val TAG = "QuizViewModel"

class QuizViewModel(quizListModel: QuizListModel, currentUser: User) : ViewModel() {

    private var firebaseRepository = FirebaseRepository()

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val quizDetail = quizListModel

    private val quizId = quizListModel.quiz_id

    private val _currentUser = currentUser

    private var allQuestionList = mutableListOf<QuestionsModel>()
    private val totalQuestionToAnswer = quizListModel.questions
    private val questionsToAnswer = mutableListOf<QuestionsModel>()

    // Question Title
    private val _quizTitle = MutableLiveData<String>()
    val quizTitle: LiveData<String>
        get() = _quizTitle

    // Question number
    private val _questionNumber = MutableLiveData<Int>()
    val questionNumber: LiveData<Int>
        get() = _questionNumber

    val questionNumberString: LiveData<String> = Transformations.map(_questionNumber) {
        it?.toString()
    }

    // Question time
    private lateinit var timer: CountDownTimer

    private val _questionTime = MutableLiveData<String>()
    val questionTime: LiveData<String>
        get() = _questionTime

    // Question time Progress bar
    private val _questionProgress = MutableLiveData<Int>()
    val questionProgress: LiveData<Int>
        get() = _questionProgress

    // Question text
    private val _questionText = MutableLiveData<String>()
    val questionText: LiveData<String>
        get() = _questionText

    // Options
    private val _optionA = MutableLiveData<String>()
    val optionA: LiveData<String>
        get() = _optionA

    private val _optionB = MutableLiveData<String>()
    val optionB: LiveData<String>
        get() = _optionB

    private val _optionC = MutableLiveData<String>()
    val optionC: LiveData<String>
        get() = _optionC

    private val _optionD = MutableLiveData<String>()
    val optionD: LiveData<String>
        get() = _optionD

    private val _isTimeUp = MutableLiveData<Boolean>()
    val isTimeUp: LiveData<Boolean>
        get() = _isTimeUp

    private val _shouldNavigateToResult = MutableLiveData<Boolean>()
    val shouldNavigateToResult: LiveData<Boolean>
        get() = _shouldNavigateToResult

    // Can answer
    private var canAnswer: Boolean = false
    private var correctAnswer: Int = 0
    private var wrongAnswer: Int = 0
    private var currentQuestionNumber: Int = 1
    private var notAnswered: Int = 0

    init {
        _quizTitle.value = quizDetail.name
        initializeQuestions()
    }

    private fun initializeQuestions() {
        uiScope.launch {
            fetchQuestions()
        }
    }

    private suspend fun fetchQuestions() {
        withContext(Dispatchers.IO) {
            firebaseRepository.getQuestion(quizId).addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@EventListener
                }

                val questionModelList: MutableList<QuestionsModel> = mutableListOf()
                for (doc in value!!) {
                    val questionItem = doc.toObject(QuestionsModel::class.java)
                    questionModelList.add(questionItem)
                }
                allQuestionList = questionModelList
                pickQuestions()
                loadQuestion(currentQuestionNumber)
            })
        }
    }

    fun loadNextQuestion() {
        currentQuestionNumber++

        if (currentQuestionNumber > totalQuestionToAnswer) {
            submitResults()
        } else {
            loadQuestion(currentQuestionNumber)
        }
    }

    private fun submitResults() {
        uiScope.launch {
            val resultMap = HashMap<String, Any?>()
            resultMap["correct"] = correctAnswer
            resultMap["wrong"] = wrongAnswer
            resultMap["unanswered"] = notAnswered

            resultMap["quiz_name"] = quizDetail.name
            resultMap["quiz_category"] = quizDetail.category

            resultMap["player_id"] = _currentUser.userId
            resultMap["player_name"] = _currentUser.name
            resultMap["player_photo"] = if (_currentUser.imageUrl != null && Uri.EMPTY != _currentUser.imageUrl) _currentUser.imageUrl.toString() else _currentUser.imageUrl

            submit(resultMap)
        }
    }

    private suspend fun submit(resultMap: HashMap<String, Any?>) {
        withContext(Dispatchers.IO) {
            firebaseRepository.getResultsByQuizId(quizId).document(_currentUser.userId).set(resultMap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Goto result page
                        navigateToResultPage()
                    } else {
                        // Show error
                        _quizTitle.value = it.exception?.message
                    }
                }
        }
    }

    private fun loadQuestion(questionNumber: Int) {
        // Question number
        _questionNumber.value = questionNumber

        // Load Question
        _questionText.value = questionsToAnswer[questionNumber - 1].question

        shuffleChoices(questionNumber)

        // Question Loaded. Set Can answer
        canAnswer = true
        currentQuestionNumber = questionNumber

        // Start Question Timer
        startTimer(questionNumber)
    }

    private fun shuffleChoices(questionNumber: Int) {
        // Clear choices
        _optionA.value = null
        _optionB.value = null
        _optionC.value = null
        _optionD.value = null

        var answers = listOf(
            questionsToAnswer[questionNumber - 1].option_a,
            questionsToAnswer[questionNumber - 1].option_b,
            questionsToAnswer[questionNumber - 1].option_c,
            questionsToAnswer[questionNumber - 1].option_d
        ).toMutableList()

        answers = answers.filter{ it.isNotEmpty() }.toMutableList()

        for (i in 0 until answers.size) {
            val j = (0..i).random()

            val temp: String = answers[i]
            answers[i] = answers[j]
            answers[j] = temp
        }

        mutableListOf(_optionA, _optionB, _optionC, _optionD).forEachIndexed { index, it ->
            if(index <= answers.size-1){
                it.value = answers[index]
            }
        }
    }


    private fun startTimer(questionNumber: Int) {
        // Set Timer text
        val timeToAnswer = questionsToAnswer[questionNumber - 1].timer
        _questionTime.value = timeToAnswer.toString()

        // Start Countdown
        timer = object : CountDownTimer(timeToAnswer * 1000, 10) {
            override fun onFinish() {
                // Time up
                canAnswer = false
                notAnswered++
                onTimeUp()
            }

            override fun onTick(millisUntilFinished: Long) {
                _questionTime.value = (millisUntilFinished / 1000).toString()

                val percent = millisUntilFinished / (timeToAnswer * 10)
                _questionProgress.value = percent.toInt()
            }
        }

        timer.start()
    }

    private fun pickQuestions() {
        for (i in 0..totalQuestionToAnswer.toInt()) {
            val randomNumber = getRandomInteger(allQuestionList.size)
            questionsToAnswer.add(allQuestionList.get(randomNumber))
             allQuestionList.removeAt(randomNumber)

            Log.d(TAG, "Questions $i" + questionsToAnswer[i].question)
        }
    }

    private fun getRandomInteger(maximum: Int, minimum: Int = 0): Int {
        return ((Math.random() * (maximum - minimum))).toInt() + minimum
    }

    fun getCorrectAnswer(selectedAnswer: String): String {
        //Check answer
        if (canAnswer) {
            if (questionsToAnswer[currentQuestionNumber - 1].answer == selectedAnswer) {
                // Correct answer
                correctAnswer++
                Log.d(TAG, "Correct answer")
            } else {
                wrongAnswer++
                Log.d(TAG, "Wrong answer")
            }
            // Set can answer to false
            canAnswer = false
            // stop the timer
            timer.cancel()
        }

        return questionsToAnswer[currentQuestionNumber - 1].answer
    }

    private fun onTimeUp() {
        _isTimeUp.value = true
    }

    fun onTimeUpComplete() {
        _isTimeUp.value = false
    }

    private fun navigateToResultPage() {
        _shouldNavigateToResult.value = true
    }

    fun navigateToResultPageComplete() {
        _shouldNavigateToResult.value = false
    }

    fun canAnswer(): Boolean {
        return canAnswer
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}