package com.heathkev.quizapp.ui.quiz

import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.heathkev.quizapp.data.QuestionsModel
import com.heathkev.quizapp.data.QuizListModel
import com.heathkev.quizapp.firebase.FirebaseRepository

private const val TAG = "QuizViewModel"
class QuizViewModel(quizListModel: QuizListModel) : ViewModel() {

    private var firebaseRepository = FirebaseRepository()

    private val quizDetail = quizListModel

    private val quizId = quizListModel.quiz_id
    val quizTitle = quizDetail.name

    private var allQuestionList = mutableListOf<QuestionsModel>()
    private val totalQuestionToAnswer = quizListModel.questions
    private val questionsToAnswer = mutableListOf<QuestionsModel>()

    // Quetion number
    private val _questionNumber = MutableLiveData<String>()
    val questionNumber: LiveData<String>
        get() = _questionNumber

    // Question time
    private val _questionTime = MutableLiveData<String>()
    val questionTime: LiveData<String>
        get() = _questionTime

    // Question time Progress bar
    private val _questionProgress = MutableLiveData<Int>()
    val questionProgress: LiveData<Int>
        get() = _questionProgress

    // Question time Progress bar
    private val _questionProgressVisibility = MutableLiveData<Int>()
    val questionProgressVisibility: LiveData<Int>
        get() = _questionProgressVisibility

    // Question text
    private val _questionText = MutableLiveData<String>()
    val questionText: LiveData<String>
        get() = _questionText

    // Options
    private val _optionVisibility = MutableLiveData<Int>()
    val optionVisibility: LiveData<Int>
        get() = _optionVisibility

    private val _optionsAvailability = MutableLiveData<Boolean>()
    val optionsAvailability: LiveData<Boolean>
        get() = _optionsAvailability

    private val _optionA = MutableLiveData<String>()
    val optionA : LiveData<String>
        get() = _optionA

    private val _optionB = MutableLiveData<String>()
    val optionB : LiveData<String>
        get() = _optionB

    private val _optionC = MutableLiveData<String>()
    val optionC : LiveData<String>
        get() = _optionC

    // Next & Feedback Buttons
    private val _buttonVisibility = MutableLiveData<Int>()
    val buttonVisibility: LiveData<Int>
        get() = _buttonVisibility

    private val _buttonsAvailability = MutableLiveData<Boolean>()
    val buttonsAvailability: LiveData<Boolean>
        get() = _buttonsAvailability

    // Can answer
    private var canAnswer: Boolean = false

    private var currentQuestion: Int = 0

    init {
        fetchQuestions()
    }

    private fun fetchQuestions(){
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
            loadUI()
        })
    }

    fun loadUI(){
        // Enable Options
        _optionVisibility.value = View.VISIBLE
        _optionsAvailability.value = true
        _buttonVisibility.value = View.INVISIBLE
        _buttonsAvailability.value = false

        // Load Question
        loadQuestion(1)
    }

    private fun loadQuestion(questionNumber: Int){
        // Question number
        _questionNumber.value = questionNumber.toString()

        // Load Question
        _questionText.value =  questionsToAnswer[questionNumber].question

        // Load Options
        _optionA.value = questionsToAnswer[questionNumber].option_a
        _optionB.value = questionsToAnswer[questionNumber].option_b
        _optionC.value = questionsToAnswer[questionNumber].option_c

        // Question Loaded. Set Can answer
        canAnswer = true
        currentQuestion = questionNumber

        // Start Question Timer
        startTimer(questionNumber)
    }

    private fun startTimer(questionNumber: Int) {

        // Set Timer text
        val timeToAnswer = questionsToAnswer[questionNumber].timer
        _questionTime.value = timeToAnswer.toString()

        //Show Timer Progress bar
        _questionProgressVisibility.value = View.VISIBLE

        // Start Countdown
        val timer = object: CountDownTimer(timeToAnswer*1000,10){
            override fun onFinish() {
                // Time up
                canAnswer = false
            }

            override fun onTick(millisUntilFinished: Long) {
                _questionTime.value = (millisUntilFinished / 1000).toString()

                val percent = millisUntilFinished/(timeToAnswer*10)
                _questionProgress.value = percent.toInt()
            }
        };

        timer.start()
    }

    fun pickQuestions(){
        for (i in 0..totalQuestionToAnswer.toInt()){
            val randomNumber = getRandomInteger(allQuestionList.size)
            questionsToAnswer.add(allQuestionList.get(randomNumber))
            // allQuestionList.removeAt(randomNumber)

            Log.d(TAG,"Questions $i" + questionsToAnswer[i].question)
        }
    }

    private fun getRandomInteger(maximum: Int, minimum: Int = 0): Int{
        return ((Math.random()*(maximum - minimum))).toInt() + minimum
    }

    fun CheckSelectedAnswer(selectedAnswer: String){
        //Check answer
        if(canAnswer){
            if(questionsToAnswer[currentQuestion].answer == selectedAnswer){
                // Correct answer
                Log.d(TAG, "Correct answer")
            }
            else{
                Log.d(TAG, "Wrong answer")
            }
            canAnswer = false
        }
    }
}