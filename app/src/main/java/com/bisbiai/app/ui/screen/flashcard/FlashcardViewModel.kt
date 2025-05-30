package com.bisbiai.app.ui.screen.flashcard

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bisbiai.app.core.utils.Resource
import com.bisbiai.app.core.utils.saveByteArrayToFile
import com.bisbiai.app.domain.repository.AzureRepository
import com.bisbiai.app.domain.usecases.GetFlashcardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val getFlashcardUseCase: GetFlashcardUseCase,
    private val azureRepository: AzureRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FlashcardState())
    val state: StateFlow<FlashcardState> = _state.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    init {
        loadFlashcards()
    }

    fun loadFlashcards() = viewModelScope.launch {
        val flashcards = getFlashcardUseCase()
        _state.update { it.copy(flashcards = flashcards) }
    }

    fun playAudio(text: String, context: Context) = viewModelScope.launch {
        if (_isLoading.value) return@launch

        _isLoading.update { true }

        val result = azureRepository.textToSpeech(text)
        when (result) {
            is Resource.Success -> {
                _isLoading.update { false }
                result.data?.let { audioBytes ->
                    val audioFile = saveByteArrayToFile(context, audioBytes)
                    playAudioFile(audioFile)
                }
            }
            is Resource.Error -> {
                _errorMessage.emit(result.message ?: "Something went wrong!")
                _isLoading.update { false }
            }
            is Resource.Loading -> {
                // Tampilkan loading
            }
        }
    }

    fun playAudioFile(file: File) {
        val mediaPlayer = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            prepare()
            start()
        }

        mediaPlayer.setOnCompletionListener {
            it.release() // penting untuk menghindari memory leak
        }
    }
}