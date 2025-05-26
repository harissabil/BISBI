package com.bisbiai.app.ui.screen.scenarios

import androidx.lifecycle.ViewModel
import com.bisbiai.app.domain.repository.AzureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ScenariosViewModel @Inject constructor(
    private val azureRepository: AzureRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ScenariosState())
    val state: StateFlow<ScenariosState> = _state.asStateFlow()

    fun onSituationDescriptionChanged(description: String) {
        _state.update {
            it.copy(situationDescription = description)
        }
    }
}