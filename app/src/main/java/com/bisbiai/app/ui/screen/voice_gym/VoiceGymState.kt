package com.bisbiai.app.ui.screen.voice_gym

import com.bisbiai.app.data.remote.dto.PronunciationAssessmentResponse

data class VoiceGymState(
    val phraseToPractice: String = "",
    val pronunciationAssessmentResponse: PronunciationAssessmentResponse? = null,
    val isRecording: Boolean = false,
    val recordingPath: String? = null,
    val isLoading: Boolean = false,
)
