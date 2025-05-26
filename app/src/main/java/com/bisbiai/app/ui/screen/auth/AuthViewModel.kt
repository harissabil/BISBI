package com.bisbiai.app.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bisbiai.app.core.utils.Resource
import com.bisbiai.app.data.auth.dto.SignedInResponse
import com.bisbiai.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    fun onContinueWithGoogle(token: String) {
        _state.value = _state.value.copy(
            isLoading = true,
            isInSignInProcess = true
        )
        viewModelScope.launch {
            val signInResult = authRepository.signInWithGoogle(token)
            onSignInResult(signInResult)
        }
    }

    private fun onSignInResult(result: Resource<SignedInResponse>) {
        Timber.e("onSignInResult: ${result.data}")
        viewModelScope.launch {
            when (result) {
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isSuccessful = false,
                        isLoading = false,
                        isInSignInProcess = false
                    )
                    _errorMessage.emit(
                        result.message ?: "Something went wrong, please try again."
                    )
                }

                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true, isInSignInProcess = false)
                }

                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        isSuccessful = true,
                        isLoading = false,
                        isInSignInProcess = false,
                    )
                }
            }
        }
    }
}