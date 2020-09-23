package com.heathkev.quizado.firebase

import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FirebaseRepository"

@Singleton
class FirebaseRepository @Inject constructor(){

    private val firebaseFireStore = FirebaseFirestore.getInstance()

    suspend fun registerUser(userId: String, userMap: HashMap<String, Any?>): Void? {
        return firebaseFireStore.collection("Users").document(userId).set(userMap).await()
    }

    suspend fun getQuizList(): QuerySnapshot? {
        return firebaseFireStore.collection("QuizList").get().await()
    }

    suspend fun getRecommendedQuiz(userId: String): QuerySnapshot? {
        return firebaseFireStore.collection("feeds").document(userId).collection("recommedations").get().await()
    }
    suspend fun getMostPopularQuiz(userId: String): QuerySnapshot? {
        return firebaseFireStore.collection("feeds").document(userId).collection("popular").get().await()
    }

    suspend fun getQuizList(category: String?): QuerySnapshot? {
        return firebaseFireStore.collection("QuizList").whereEqualTo("category", category).get().await()
    }

    suspend fun getSingleQuiz(): QuerySnapshot? {
        return firebaseFireStore.collection("QuizList").limit(1).get().await()
    }

    suspend fun getQuizQuestions(quizId: String): QuerySnapshot? {
        return firebaseFireStore.collection("QuizList").document(quizId).collection("Questions").get().await()
    }

    suspend fun submitQuizResult(quizId: String, userId: String, result: HashMap<String, Any?>): Void? {
        return firebaseFireStore.collection("QuizList").document(quizId).collection("Results").document(userId).set(result).await()
    }

    suspend fun getResultsByQuizId(quizId: String, userId: String): DocumentSnapshot? {
        return firebaseFireStore.collection("QuizList").document(quizId).collection("Results").document(userId).get().await()
    }

    suspend fun getAllResults(): QuerySnapshot? {
        return firebaseFireStore.collectionGroup("Results").get().await()
    }

    suspend fun getResultsByUserId(userId: String): QuerySnapshot? {
        return firebaseFireStore.collectionGroup("Results").whereEqualTo("player_id", userId)
            .get()
            .await()
    }

    suspend fun sendQuestion(questionMap : HashMap<String, Any?>): Void? {
        return firebaseFireStore.collection("QuestionRequest").document().set(questionMap).await()
    }
}