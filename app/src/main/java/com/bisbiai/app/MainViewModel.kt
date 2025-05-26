package com.bisbiai.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bisbiai.app.domain.repository.AuthRepository
import com.bisbiai.app.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    var splashCondition by mutableStateOf(true)
        private set

    private val _startDestination = MutableStateFlow<Route>(Route.Splash)
    val startDestination: StateFlow<Route> = _startDestination.asStateFlow()

    init {
        getStartDestination()
    }

    private fun getStartDestination() = viewModelScope.launch {
        delay(700)
        val isUserSignedIn = authRepository.getSignedInUser().data != null

        _startDestination.value = if (isUserSignedIn) {
            Route.Home
        } else {
            Route.Auth
        }

        splashCondition = false
    }
}