package com.heathkev.quizapp.data

import com.google.firebase.firestore.DocumentId

class QuizListModel(
    @DocumentId
    val quiz_id: String,

    val name: String = "",
    val desc: String = "",
    val image: String = "",
    val level: String = "",
    val visibility: String = "",
    val questions: Long = 0L) {

}