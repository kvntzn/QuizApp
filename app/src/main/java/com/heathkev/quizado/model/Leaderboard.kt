package com.heathkev.quizado.model

import com.google.firebase.firestore.DocumentId

class Leaderboard(
    @DocumentId
    val userId: String,
    val score: Long,
    val name: String,
    val photo: String
) {

    constructor() : this("", 0L, "", "")
}
