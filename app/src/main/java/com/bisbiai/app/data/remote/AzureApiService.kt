package com.bisbiai.app.data.remote

import com.bisbiai.app.data.remote.dto.DetectObjectsResponse
import com.bisbiai.app.data.remote.dto.GenerateLessonRequest
import com.bisbiai.app.data.remote.dto.GenerateLessonResponse
import com.bisbiai.app.data.remote.dto.GetObjectDetailsResponse
import com.bisbiai.app.data.remote.dto.PronunciationAssessmentResponse
import com.bisbiai.app.data.remote.dto.TextToSpeechRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AzureApiService {
    @Multipart
    @POST("/api/DetectObjectsVisual")
    suspend fun detectObjects(
        @Part image: MultipartBody.Part
    ): Response<List<DetectObjectsResponse>>

    @Multipart
    @POST("/api/GetObjectDetailsVisual")
    suspend fun getObjectDetails(
        @Part image: MultipartBody.Part
    ): Response<GetObjectDetailsResponse>

    @POST("/api/GetTTSAudio")
    suspend fun textToSpeech(
        @Body request: TextToSpeechRequest
    ): Response<ResponseBody>

    @POST("/api/GenerateLesson")
    suspend fun generateLesson(
        @Body request: GenerateLessonRequest
    ): Response<GenerateLessonResponse>

    @Multipart
    @POST("/api/PronunciationAssessmentFunc")
    suspend fun assessPronunciation(
        @Part audio: MultipartBody.Part,
        @Part("referenceText") referenceText: RequestBody,
        @Part("languageCode") languageCode: RequestBody
    ): Response<PronunciationAssessmentResponse>
}