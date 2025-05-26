package com.bisbiai.app.ui.screen.voice_gym

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bisbiai.app.core.utils.Resource
import com.bisbiai.app.core.utils.saveByteArrayToFile
import com.bisbiai.app.domain.repository.AzureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class VoiceGymViewModel @Inject constructor(
    private val azureRepository: AzureRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(VoiceGymState())
    val state: StateFlow<VoiceGymState> = _state.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    private var recorder: MediaRecorder? = null
    private var audioFile: File? = null

    fun setReferenceText(text: String) {
        _state.update { it.copy(phraseToPractice = text) }
    }

    fun startRecording(context: Context) {
        // Check for permission first
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            _errorMessage.tryEmit("Microphone permission is required for recording")
            return
        }

        if (_state.value.phraseToPractice.isBlank()) {
            _errorMessage.tryEmit("Please set a phrase to practice before recording.")
            return
        }

        try {
            // Create temporary file for recording
            val outputDir = context.cacheDir
            audioFile = File.createTempFile("audio_recording", ".wav", outputDir)
            val filePath = audioFile?.absolutePath ?: return

            // Initialize recorder based on API level
            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                // Use proper WAV format
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)  // or .WEBM
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)     // better quality
                setAudioSamplingRate(44100)                                // CD quality
                setAudioEncodingBitRate(128000)                                  // Good quality bitrate
                setOutputFile(filePath)

                try {
                    prepare()
                    start()
                    _state.update {
                        it.copy(
                            isRecording = true,
                            recordingPath = filePath,
                        )
                    }
                } catch (e: IOException) {
                    _errorMessage.tryEmit("Error preparing recorder: ${e.message}")
                }
            }
        } catch (e: Exception) {
            _errorMessage.tryEmit("Error starting recording: ${e.message}")
            _state.update { it.copy(isRecording = false) }
            recorder?.release()
            recorder = null
            audioFile = null
        }
    }

    fun stopRecording() {
        if (!_state.value.isRecording) return

        try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            _state.update { it.copy(isRecording = false) }

            // After stopping, send for assessment
            audioFile?.let { file ->
                assessPronunciation(file)
            }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isRecording = false,
                )
            }
            _errorMessage.tryEmit("Error stopping recording: ${e.message}")
        }
    }

    private fun assessPronunciation(audioFile: File) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Create multipart request
                val requestFile = audioFile.asRequestBody("audio/wav".toMediaTypeOrNull())
                val audioPart =
                    MultipartBody.Part.createFormData("audio", audioFile.name, requestFile)

                // Get reference text from state
                val referenceText = _state.value.phraseToPractice

                // Call repository method
                val result = azureRepository.assessPronunciation(
                    audio = audioPart,
                    referenceText = referenceText,
                    languageCode = "en-US" // Default to English, could be made configurable
                )

                when (result) {
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                pronunciationAssessmentResponse = result.data,
                                isLoading = false
                            )
                        }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false
                            )
                        }
                        _errorMessage.emit("Error assessing pronunciation: ${result.message ?: "Unknown error"}")
                    }

                    is Resource.Loading -> {}
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false
                    )
                }
                _errorMessage.emit("Error during pronunciation assessment: ${e.message ?: "Unknown error"}")
            }
        }
    }

    fun dismissDialog() {
        _state.update { it.copy(
            pronunciationAssessmentResponse = null,
            isRecording = false,
            recordingPath = null
        ) }
    }

    override fun onCleared() {
        // Clean up recorder if ViewModel is destroyed
        recorder?.release()
        recorder = null
        super.onCleared()
    }

    fun playAudio(text: String, context: Context) = viewModelScope.launch {
        if (_state.value.isLoading) return@launch

        _state.update {  it.copy(isLoading = true) }

        val result = azureRepository.textToSpeech(text)
        when (result) {
            is Resource.Success -> {
                _state.update { it.copy(isLoading = false) }
                result.data?.let { audioBytes ->
                    val audioFile = saveByteArrayToFile(context, audioBytes)
                    playAudioFile(audioFile)
                }
            }
            is Resource.Error -> {
                _errorMessage.emit(result.message ?: "Something went wrong!")
                _state.update { it.copy(isLoading = false) }
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

    fun showError(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }
}