package com.bisbiai.app.data.auth

import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.bisbiai.app.core.utils.Resource
import com.bisbiai.app.data.auth.dto.SignedInResponse
import com.bisbiai.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.CancellationException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val credentialManager: CredentialManager,
    private val auth: FirebaseAuth,
) : AuthRepository {
    override suspend fun signInWithGoogle(token: String): Resource<SignedInResponse> {
        val googleCredentials = GoogleAuthProvider.getCredential(token, null)

        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            Resource.Success(
                data = user?.run {
                    SignedInResponse(
                        userId = uid,
                        userName = displayName,
                        email = email,
                        profilePictureUrl = photoUrl?.toString(),
                    )
                }
            )
        } catch (e: Exception) {
            Timber.e("signInWithIntent: ${e.message}")
            e.printStackTrace()
            if (e is CancellationException) throw e
            Resource.Error(
                data = null,
                message = e.message
            )
        }
    }

    override suspend fun signOut(): Resource<Boolean> {
        return try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            auth.signOut()
            Resource.Success(data = true)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Resource.Error(
                data = false,
                message = e.message
            )
        }
    }

    override fun getSignedInUser(): Resource<SignedInResponse> {
        val user = auth.currentUser
        Timber.e("getSignInUser: ${user?.displayName}")
        return if (user != null) {
            Resource.Success(
                data = SignedInResponse(
                    userId = user.uid,
                    userName = user.displayName,
                    email = user.email,
                    profilePictureUrl = user.photoUrl.toString(),
                )
            )
        } else {
            Resource.Error(
                data = null,
                message = "User is not signed in."
            )
        }
    }
}