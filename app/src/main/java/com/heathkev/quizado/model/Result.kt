package com.heathkev.quizado.model

import com.google.firebase.firestore.DocumentId

class Result (
    @DocumentId
    val user_id: String,
    val player_name: String?,
    val player_photo: String?,
    val quiz_name: String,
    val quiz_category: String,
    val correct: Long,
    val unanswered: Long,
    val wrong: Long
){
    constructor() : this("","","","","",0L,0L,0L)
}