package com.heathkev.quizado.data

import android.net.Uri

class User(
    val userId: String = "",
    val name: String? = "",
    val imageUrl: Uri? = null,
    val email: String? = ""
)