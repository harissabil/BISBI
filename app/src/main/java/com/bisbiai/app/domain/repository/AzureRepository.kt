package com.bisbiai.app.domain.repository

import com.bisbiai.app.core.utils.Resource
import com.bisbiai.app.data.remote.dto.DetectObjectsResponse
import com.bisbiai.app.data.remote.dto.GenerateLessonResponse
import com.bisbiai.app.data.remote.dto.GetObjectDetailsResponse
import com.bisbiai.app.data.remote.dto.PronunciationAssessmentResponse
import okhttp3.MultipartBody

interface AzureRepository {
    suspend fun detectObjects(image: MultipartBody.Part): Resource<List<DetectObjectsResponse>>
    suspend fun getObjectDetails(image: MultipartBody.Part): Resource<GetObjectDetailsResponse>
    suspend fun textToSpeech(text: String): Resource<ByteArray>
    suspend fun generateLesson(
        scenarioDescription: String,
        userProficiencyLeve: String,
    ): Resource<GenerateLessonResponse>
    suspend fun assessPronunciation(
        audio: MultipartBody.Part,
        referenceText: String,
        languageCode: String = "en-US"
    ): Resource<PronunciationAssessmentResponse>
}