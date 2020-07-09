package com.heathkev.quizado.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.heathkev.quizado.data.QuizListModel

private const val TAG = "FirebaseRepository"

class FirebaseRepository {

    private val firebaseFireStore = FirebaseFirestore.getInstance()
    private val quizRef = firebaseFireStore.collection("QuizList").whereEqualTo("visibility", "public")

    fun getQuizList(): CollectionReference {
        return firebaseFireStore.collection("QuizList")
    }

    fun getQuestion(quizId: String): CollectionReference {
        return firebaseFireStore.collection("QuizList").document(quizId).collection("Questions")
    }

    fun getResults(quizId: String) : CollectionReference{
       return firebaseFireStore.collection("QuizList").document(quizId).collection("Results")
    }
}