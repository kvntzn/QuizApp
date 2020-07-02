package com.heathkev.quizapp.firebase

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository {

    private val firebaseFireStore = FirebaseFirestore.getInstance()
    private val quizRef = firebaseFireStore.collection("QuizList")

    fun getQuizData(): CollectionReference {
        return quizRef
    }
}