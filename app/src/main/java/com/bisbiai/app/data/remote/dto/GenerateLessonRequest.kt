package com.bisbiai.app.data.remote.dto

data class GenerateLessonRequest(
    val scenarioDescription: String,
    val userNativeLanguageCode: String = "id",
    val learningLanguageCode: String = "en",
    val userProficiencyLevel: String
)