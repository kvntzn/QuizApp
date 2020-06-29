package com.heathkev.quizapp.firebase

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.heathkev.quizapp.data.QuizListModel

class FirebaseRepository(private val onFireStoreTaskComplete: OnFireStoreTaskComplete) {

    private val firebaseFireStore = FirebaseFirestore.getInstance()
    private val quizRef = firebaseFireStore.collection("QuizList")


    fun getQuizData() : Unit{
        quizRef.get().addOnCompleteListener(OnCompleteListener {
            if(it.isSuccessful){
                it.result?.toObjects(QuizListModel::class.java)?.let { quizList ->
                    onFireStoreTaskComplete.quizListDataAdded(
                        quizList
                    )
                }
            }else{
                onFireStoreTaskComplete.onError(it.exception)
            }
        })
    }

    interface OnFireStoreTaskComplete{
        fun quizListDataAdded(quizListModelsList: List<QuizListModel>)
        fun onError(e: Exception?)
    }
}