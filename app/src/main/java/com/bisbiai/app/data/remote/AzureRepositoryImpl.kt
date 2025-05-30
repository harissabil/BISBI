package com.bisbiai.app.data.remote

import com.bisbiai.app.core.utils.Resource
import com.bisbiai.app.data.remote.dto.DetectObjectsResponse
import com.bisbiai.app.data.remote.dto.GenerateLessonRequest
import com.bisbiai.app.data.remote.dto.GenerateLessonResponse
import com.bisbiai.app.data.remote.dto.GetObjectDetailsResponse
import com.bisbiai.app.data.remote.dto.PronunciationAssessmentResponse
import com.bisbiai.app.data.remote.dto.TextToSpeechRequest
import com.bisbiai.app.domain.repository.AzureRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

class AzureRepositoryImpl @Inject constructor(
    private val azureApiService: AzureApiService,
) : AzureRepository {
    override suspend fun detectObjects(image: MultipartBody.Part): Resource<DetectObjectsResponse> =
        try {
            val response = azureApiService.detectObjects(image)
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("No data received")
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val json = JSONObject(errorBody ?: "")
                    val error = json.optString("error")
                    val details = json.optString("details")
                    if (details.isNotEmpty()) "$error\nDetails: $details" else error
                } catch (_: Exception) {
                    "Error: ${response.code()} - ${response.message()}"
                }
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Failed to detect objects: ${e.message}")
        }

    override suspend fun getObjectDetails(image: MultipartBody.Part): Resource<GetObjectDetailsResponse> =
        try {
            val response = azureApiService.getObjectDetails(image)
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("No data received")
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val json = JSONObject(errorBody ?: "")
                    val error = json.optString("error")
                    val details = json.optString("details")
                    if (details.isNotEmpty()) "$error\nDetails: $details" else error
                } catch (_: Exception) {
                    "Error: ${response.code()} - ${response.message()}"
                }

                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Failed to get object details: ${e.message}")
        }

    override suspend fun textToSpeech(text: String): Resource<ByteArray> =
        try {
            val request = TextToSpeechRequest(text)
            val response = azureApiService.textToSpeech(request)
            if (response.isSuccessful) {
                val bytes = response.body()?.bytes()
                if (bytes != null) {
                    Resource.Success(bytes)
                } else {
                    Resource.Error("Audio data is null")
                }
            } else {
                Resource.Error("Error ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.localizedMessage}")
        }

    override suspend fun generateLesson(
        scenarioDescription: String,
        userProficiencyLevel: String,
    ): Resource<GenerateLessonResponse> =
        try {
            val response = azureApiService.generateLesson(
                GenerateLessonRequest(
                    scenarioDescription = scenarioDescription,
                    userProficiencyLevel = userProficiencyLevel
                )
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("No data received")
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val json = JSONObject(errorBody ?: "")
                    val error = json.optString("error")
                    val details = json.optString("details")
                    if (details.isNotEmpty()) "$error\nDetails: $details" else error
                } catch (_: Exception) {
                    "Error: ${response.code()} - ${response.message()}"
                }
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Failed to generate lesson: ${e.message}")
        }

    override suspend fun assessPronunciation(
        audio: MultipartBody.Part,
        referenceText: String,
        languageCode: String,
    ): Resource<PronunciationAssessmentResponse> =
        try {
            val referenceTextBody = referenceText.toRequestBody("text/plain".toMediaTypeOrNull())
            val languageCodeBody = languageCode.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = azureApiService.assessPronunciation(
                audio = audio,
                referenceText = referenceTextBody,
                languageCode = languageCodeBody
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("No data received")
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val json = JSONObject(errorBody ?: "")
                    val error = json.optString("error")
                    val details = json.optString("details")
                    if (details.isNotEmpty()) "$error\nDetails: $details" else error
                } catch (_: Exception) {
                    "Error: ${response.code()} - ${response.message()}"
                }

                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Failed to assess pronunciation: ${e.message}")
        }
}