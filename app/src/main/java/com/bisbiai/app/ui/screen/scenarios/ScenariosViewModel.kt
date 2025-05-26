package com.bisbiai.app.ui.screen.scenarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bisbiai.app.core.utils.Resource
import com.bisbiai.app.data.mapper.toGenerateLessonResponse
import com.bisbiai.app.data.mapper.toScenarioEntity
import com.bisbiai.app.domain.repository.AzureRepository
import com.bisbiai.app.domain.repository.ObjectDetectionDbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ScenariosViewModel @Inject constructor(
    private val azureRepository: AzureRepository,
    private val objectDetectionDbRepository: ObjectDetectionDbRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ScenariosState())
    val state: StateFlow<ScenariosState> = _state.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    init {
        getScenarioHistories()
    }

    fun onSituationDescriptionChanged(description: String) {
        _state.update {
            it.copy(situationDescription = description)
        }
    }

    fun getScenarioLesson(userProficiencyLevel: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        val result = azureRepository.generateLesson(
            _state.value.situationDescription,
            userProficiencyLevel
        )

        when (result) {
            is Resource.Error -> {
                _state.update { it.copy(isLoading = false) }
                _errorMessage.emit(result.message ?: "Something went wrong!")
            }

            is Resource.Loading -> {}
            is Resource.Success -> {
                val scenarioId = result.data?.let {
                    objectDetectionDbRepository.saveScenario(
                        scenarioEntity = it.toScenarioEntity()
                    )
                }
                Timber.d("Scenario saved with ID: $scenarioId")

                _state.update {
                    it.copy(
                        isLoading = false,
                        lessonData = result.data,
                        situationDescription = "",
                        isGoingToDetail = true
                    )
                }
            }
        }
    }

    fun resetState() {
        _state.update {
            it.copy(
                situationDescription = "",
                lessonData = null,
                isGoingToDetail = false
            )
        }
    }

    fun getScenarioHistories() = viewModelScope.launch {
        objectDetectionDbRepository.getAllScenarios().collect { scenarios ->
            _state.update { it.copy(scenarioHistory = scenarios) }
        }
    }

    fun onScenarioClicked(scenarioId: Long) = viewModelScope.launch {
        val scenarioLesson = objectDetectionDbRepository.getScenarioById(scenarioId)
        _state.update {
            it.copy(
                lessonData = scenarioLesson?.toGenerateLessonResponse(),
                isGoingToDetail = true
            )
        }
    }
}