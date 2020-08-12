package com.heathkev.quizado.firebase

import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FirebaseRepository"

@Singleton
class FirebaseRepository @Inject constructor(){

    private val firebaseFireStore = FirebaseFirestore.getInstance()
    private val quizRef =
        firebaseFireStore.collection("QuizList").whereEqualTo("visibility", "public")

    fun getUsers(): CollectionReference {
        return firebaseFireStore.collection("Users")
    }

    suspend fun getQuizListAsync(): QuerySnapshot? {
        return firebaseFireStore.collection("QuizList").get().await()
    }

    suspend fun getQuizListAsync(category: String?): QuerySnapshot? {
        return firebaseFireStore.collection("QuizList").whereEqualTo("category", category).get().await()
    }

    suspend fun getSingleQuiz(): QuerySnapshot? {
        return firebaseFireStore.collection("QuizList").limit(1).get().await()
    }

    suspend fun getQuestion(quizId: String): QuerySnapshot? {
        return firebaseFireStore.collection("QuizList").document(quizId).collection("Questions").get().await()
    }

    fun getResultsByQuizId(quizId: String): CollectionReference {
        return firebaseFireStore.collection("QuizList").document(quizId).collection("Results")
    }

    suspend fun submitQuizResult(quizId: String, userId: String, result: HashMap<String, Any?>): Void? {
        return firebaseFireStore.collection("QuizList").document(quizId).collection("Results").document(userId).set(result).await()
    }

    suspend fun getResultsByQuizIdAsync(quizId: String, userId: String): DocumentSnapshot? {
        return firebaseFireStore.collection("QuizList").document(quizId).collection("Results").document(userId).get().await()
    }

    suspend fun getAllResultsAsync(): QuerySnapshot? {
        return firebaseFireStore.collectionGroup("Results").get().await()
    }

    fun getResultsByUserId(userId: String): Query {
        return firebaseFireStore.collectionGroup("Results").whereEqualTo("player_id", userId)
    }

    suspend fun getResultsByUserIdAsync(userId: String): QuerySnapshot? {
        return firebaseFireStore.collectionGroup("Results").whereEqualTo("player_id", userId)
            .get()
            .await()
    }

    fun getQuestionRequest(): CollectionReference {
        return firebaseFireStore.collection("QuestionRequest")
    }
}