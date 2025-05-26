package com.bisbiai.app.ui.screen.auth

data class AuthState(
    val isInSignInProcess: Boolean = false,
    val isSuccessful: Boolean = false,
    val isLoading: Boolean = false,
)
