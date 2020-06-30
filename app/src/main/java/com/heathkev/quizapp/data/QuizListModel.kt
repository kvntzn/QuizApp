package com.heathkev.quizapp.data

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuizListModel(
    @DocumentId
    val quiz_id: String = "",
    val name: String = "",
    val desc: String = "",
    val image: String = "",
    val level: String = "",
    val visibility: String = "",
    val questions: Long = 0L) : Parcelable {

}