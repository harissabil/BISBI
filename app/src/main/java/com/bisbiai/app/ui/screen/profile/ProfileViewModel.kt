package com.bisbiai.app.ui.screen.profile

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
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _userData = MutableStateFlow<SignedInResponse?>(null)
    val userData: StateFlow<SignedInResponse?> = _userData.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    init {
        getUserData()
    }

    fun getUserData() = viewModelScope.launch {
        val result = authRepository.getSignedInUser()
        if (result is Resource.Success) {
            _userData.value = result.data
        } else {
            _userData.value = null
            if (result is Resource.Error) {
                _errorMessage.emit(result.message ?: "Could not fetch user data. Please try again.")
            }
        }
    }
}