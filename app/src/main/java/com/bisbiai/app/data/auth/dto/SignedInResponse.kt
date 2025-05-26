package com.bisbiai.app.data.auth.dto

data class SignedInResponse(
    val userId: String,
    val userName: String?,
    val email: String?,
    val profilePictureUrl: String?,
)