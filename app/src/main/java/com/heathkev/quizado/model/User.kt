package com.heathkev.quizado.model

import android.net.Uri

class User(
    val userId: String = "",
    val name: String? = "",
    val imageUrl: Uri? = null,
    val email: String? = ""
)