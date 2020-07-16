package com.heathkev.quizado.data

import com.google.firebase.firestore.DocumentId

class Result (
    @DocumentId
    val user_id: String,
    val player_name: String,
    val player_photo: String,
    val correct: Long,
    val unanswered: Long,
    val wrong: Long
){
    constructor() : this("","","",0L,0L,0L)
}