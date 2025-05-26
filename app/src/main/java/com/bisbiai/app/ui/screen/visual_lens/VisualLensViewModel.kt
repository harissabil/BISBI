package com.bisbiai.app.ui.screen.visual_lens

import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bisbiai.app.core.utils.Resource
import com.bisbiai.app.core.utils.cropImageByBoundingBox
import com.bisbiai.app.core.utils.saveBitmapToFile
import com.bisbiai.app.data.local.entity.DetectedObjectEntity
import com.bisbiai.app.data.local.entity.ExampleSentenceEntity
import com.bisbiai.app.data.local.entity.ObjectDetailsEntity
import com.bisbiai.app.data.local.entity.RelatedAdjectiveEntity
import com.bisbiai.app.data.location.current_location.LocationTracker
import com.bisbiai.app.data.mapper.toGetObjectDetailsResponse
import com.bisbiai.app.data.remote.dto.DetectObjectsResponse
import com.bisbiai.app.domain.repository.AzureRepository
import com.bisbiai.app.domain.repository.ObjectDetectionDbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class VisualLensViewModel @Inject constructor(
    private val azureRepository: AzureRepository,
    private val locationTracker: LocationTracker,
    private val objectDetectionDbRepository: ObjectDetectionDbRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(VisualLensState())
    val state: StateFlow<VisualLensState> = _state.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    init {
        getObjectDetectionList()
    }

    fun detectObjectInImage(imageFile: File) = viewModelScope.launch {
        if (_state.value.isLoading) return@launch

        _state.update { it.copy(isLoading = true) }

        val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
        val response = azureRepository.detectObjects(imagePart)

        when (response) {
            is Resource.Error -> {
                _state.update { it.copy(isLoading = false) }
                response.message?.let { message ->
                    _errorMessage.emit(message)
                }
            }

            is Resource.Loading -> {}
            is Resource.Success -> {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(imageFile.path, options)
                val originalWidth = options.outWidth
                val originalHeight = options.outHeight

                val currentLocation = async { locationTracker.getCurrentLocation() }
                val (lat, long) = currentLocation.await()?.latitude to currentLocation.await()?.longitude
                if (lat != null && long != null) {
                    // Save detected objects to database
                    response.data?.let { detectedObjects ->
                        val detectedObjectId = objectDetectionDbRepository.saveDetectedObject(
                            detectedObject = DetectedObjectEntity(
                                detectObjects = detectedObjects,
                                imagePath = imageFile.absolutePath,
                                timestamp = Clock.System.now(),
                                lat = lat,
                                long = long
                            )
                        )
                        _state.update { it.copy(detectedObjectId = detectedObjectId) }
                    }
                } else {
                    _errorMessage.emit("Failed to get current location.")
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        detectedObjects = response.data,
                        imageFile = imageFile,
                        originalImageWidth = originalWidth,
                        originalImageHeight = originalHeight
                    )
                }
            }
        }

//        val detectObjectsResponseList = listOf(
//            DetectObjectsResponse(
//                objectName = "Luggage and bags",
//                confidence = 0.625,
//                boundingBox = BoundingBox(
//                    x = 1596,
//                    y = 1429,
//                    width = 591,
//                    height = 1026
//                )
//            ),
//            DetectObjectsResponse(
//                objectName = "Luggage and bags",
//                confidence = 0.659,
//                boundingBox = BoundingBox(
//                    x = 2336,
//                    y = 1486,
//                    width = 693,
//                    height = 1128
//                )
//            ),
//            DetectObjectsResponse(
//                objectName = "Luggage and bags",
//                confidence = 0.514,
//                boundingBox = BoundingBox(
//                    x = 296,
//                    y = 1715,
//                    width = 644,
//                    height = 1030
//                )
//            )
//        )
//
//        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
//        BitmapFactory.decodeFile(imageFile.path, options)
//        val originalWidth = options.outWidth
//        val originalHeight = options.outHeight
//
//
//        // Test Only
//        _state.update {
//            it.copy(
//                isLoading = false,
//                detectedObjects = detectObjectsResponseList,
//                imageFile = imageFile,
//                originalImageWidth = originalWidth,
//                originalImageHeight = originalHeight
//            )
//        }
    }

    fun dismissImageDialog() {
        _state.update {
            it.copy(
                imageFile = null,
                detectedObjects = null,
                originalImageWidth = null,
                originalImageHeight = null,
                detectedObjectId = null
            )
        }
    }

    fun getObjectDetails(detectObjectsResponse: DetectObjectsResponse) = viewModelScope.launch {
        if (_state.value.isDialogLoading || _state.value.isLoading) return@launch

        _state.update { it.copy(isDialogLoading = true) }

        val detectedObjectId = _state.value.detectedObjectId ?: return@launch

        val objectDetailsByDetectedObjectId =
            objectDetectionDbRepository.getObjectDetailsByDetectedObjectIdList(detectedObjectId)
        val existingObjectDetails =
            objectDetailsByDetectedObjectId.find { it.objectDetails.boundingBox == detectObjectsResponse.boundingBox }
        Timber.d("Existing object details: $existingObjectDetails")
        if (existingObjectDetails != null) {
            Timber.d("Object details already exist in database for bounding box: ${detectObjectsResponse.boundingBox}")
            _state.update {
                it.copy(
                    isDialogLoading = false,
                    objectDetails = existingObjectDetails.toGetObjectDetailsResponse(),
                    isGoingToDetails = true
                )
            }
            return@launch
        }

        val imageFile = _state.value.imageFile ?: return@launch
        val croppedBitmap = cropImageByBoundingBox(imageFile, detectObjectsResponse.boundingBox)

        if (croppedBitmap != null) {
            // Simpan bitmap ke file temporer
            val croppedFile = saveBitmapToFile(croppedBitmap)

            val requestFile = croppedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart =
                MultipartBody.Part.createFormData("image", croppedFile.name, requestFile)

            val response = azureRepository.getObjectDetails(image = imagePart)

            when (response) {
                is Resource.Error -> {
                    _state.update { it.copy(isDialogLoading = false) }
                    response.message?.let { message ->
                        _errorMessage.emit(message)
                    }
                }

                is Resource.Loading -> {}
                is Resource.Success -> {

                    if (_state.value.detectedObjectId != null) {
                        // Save object details to database
                        response.data?.let { objectDetails ->
                            objectDetectionDbRepository.saveObjectDetailsWithRelatedData(
                                objectDetails = ObjectDetailsEntity(
                                    detectedObjectId = _state.value.detectedObjectId!!,
                                    objectNameEn = objectDetails.objectName.en,
                                    objectNameId = objectDetails.objectName.id,
                                    descriptionEn = objectDetails.description.en,
                                    descriptionId = objectDetails.description.id,
                                    boundingBox = detectObjectsResponse.boundingBox,
                                ),
                                relatedAdjectives = objectDetails.relatedAdjectives.map { adjective ->
                                    RelatedAdjectiveEntity(
                                        objectDetailsId = 0,
                                        adjectiveEn = adjective.en,
                                        adjectiveId = adjective.id,
                                    )
                                },
                                exampleSentences = objectDetails.exampleSentences.map { sentence ->
                                    ExampleSentenceEntity(
                                        objectDetailsId = 0,
                                        sentenceEn = sentence.en,
                                        sentenceId = sentence.id,
                                    )
                                }
                            )
                        }
                    }

                    _state.update {
                        it.copy(
                            isDialogLoading = false,
                            objectDetails = response.data,
                            isGoingToDetails = true,
                        )
                    }
                }
            }
        } else {
            _errorMessage.emit("Failed to crop image.")
            _state.update { it.copy(isDialogLoading = false) }
        }
    }

    fun resetObjectDetails() {
        _state.update {
            it.copy(
                objectDetails = null,
                isGoingToDetails = false
            )
        }
    }

    private fun getObjectDetectionList() = viewModelScope.launch {
        objectDetectionDbRepository.getAllObjectsWithDetails().collect { objectDetectionList ->
            _state.update { it.copy(histories = objectDetectionList) }
        }
    }

    fun onHistoryClick(detectedObjectEntity: DetectedObjectEntity) {
        val imageFile = File(detectedObjectEntity.imagePath)
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(imageFile.path, options)
        val originalWidth = options.outWidth
        val originalHeight = options.outHeight

        _state.update { it.copy(
            imageFile = imageFile,
            originalImageWidth = originalWidth,
            originalImageHeight = originalHeight,
            detectedObjects = detectedObjectEntity.detectObjects,
            detectedObjectId = detectedObjectEntity.id,
        ) }
    }

    fun onHistoryDelete(detectedObjectEntity: DetectedObjectEntity) = viewModelScope.launch {
        objectDetectionDbRepository.deleteDetectedObject(detectedObjectEntity)
        _state.update { it.copy(detectedObjectId = null) }
        Timber.d("Deleted detected object with ID: ${detectedObjectEntity.id}")
    }
}