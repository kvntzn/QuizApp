package com.heathkev.quizado.firebase

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

private const val TAG = "FirebaseRepository"

class FirebaseRepository {

    private val firebaseFireStore = FirebaseFirestore.getInstance()
    private val quizRef = firebaseFireStore.collection("QuizList").whereEqualTo("visibility", "public")

    fun getUsers(): CollectionReference {
        return firebaseFireStore.collection("Users")
    }

    fun getQuizList(): CollectionReference {
        return firebaseFireStore.collection("QuizList")
    }

    fun getQuestion(quizId: String): CollectionReference {
        return firebaseFireStore.collection("QuizList").document(quizId).collection("Questions")
    }

    fun getResultsByQuizId(quizId: String) : CollectionReference{
       return firebaseFireStore.collection("QuizList").document(quizId).collection("Results")
    }

    fun getAllResults(): Query {
        return FirebaseFirestore.getInstance().collectionGroup("Results")
    }

    fun getResultsByUserId(userId: String): Query {
        return FirebaseFirestore.getInstance().collectionGroup("Results").whereEqualTo("player_id",userId)
    }
}