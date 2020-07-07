package com.heathkev.quizado.data

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuizListModel(
    @DocumentId
    val quiz_id: String,
    val name: String,
    val desc: String,
    val image: String,
    val level: String ,
    val visibility: String,
    val questions: Long) : Parcelable {

    constructor():this("","","","","","",0L)
}