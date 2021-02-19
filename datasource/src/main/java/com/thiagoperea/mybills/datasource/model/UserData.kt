package com.thiagoperea.mybills.datasource.model

import android.net.Uri

data class UserData(
    val id: String?,
    val displayName: String?,
    val email: String?,
    val photoUrl: Uri?,
)