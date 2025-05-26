package com.bisbiai.app.data.remote.dto

data class TextToSpeechRequest(
    val text: String,
    val languageCode: String = "en-US",
)