package com.heathkev.quizapp.data

import com.google.firebase.firestore.DocumentId

class QuestionsModel(@DocumentId
                     val questionId: String,
                     val question: String,
                     val option_a: String,
                     val option_b: String,
                     val option_c: String,
                     val answer: String,
                     val timer: Long) {
    
    constructor(): this("","","","","","",0L)

}