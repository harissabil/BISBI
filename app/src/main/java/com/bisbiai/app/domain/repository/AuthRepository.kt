package com.bisbiai.app.domain.repository

import com.bisbiai.app.core.utils.Resource
import com.bisbiai.app.data.auth.dto.SignedInResponse

interface AuthRepository {
    suspend fun signInWithGoogle(token: String): Resource<SignedInResponse>
    suspend fun signOut(): Resource<Boolean>
    fun getSignedInUser(): Resource<SignedInResponse>
}