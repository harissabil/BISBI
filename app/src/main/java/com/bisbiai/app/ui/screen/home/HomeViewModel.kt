package com.bisbiai.app.ui.screen.home

import android.content.Context
import android.content.IntentSender
import android.graphics.BitmapFactory
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bisbiai.app.core.utils.Resource
import com.bisbiai.app.core.utils.cropImageByBoundingBox
import com.bisbiai.app.core.utils.saveBitmapToFile
import com.bisbiai.app.data.local.entity.ExampleSentenceEntity
import com.bisbiai.app.data.local.entity.ObjectDetailsEntity
import com.bisbiai.app.data.local.entity.RelatedAdjectiveEntity
import com.bisbiai.app.data.local.relation.ObjectWithDetails
import com.bisbiai.app.data.location.LocationHelper
import com.bisbiai.app.data.mapper.toGetObjectDetailsResponse
import com.bisbiai.app.data.remote.dto.DetectObjectItem
import com.bisbiai.app.domain.repository.AzureRepository
import com.bisbiai.app.domain.repository.ObjectDetectionDbRepository
import com.bisbiai.app.domain.usecases.GetLocationUseCase
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
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
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationHelper: LocationHelper,
    private val getLocationUseCase: GetLocationUseCase,
    private val objectDetectionDbRepository: ObjectDetectionDbRepository,
    private val azureRepository: AzureRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _isLocationEnabled = MutableStateFlow(false)
    val isLocationEnabled: StateFlow<Boolean> = _isLocationEnabled.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    init {
        updateLocationServiceStatus()
        getObjectDetectionList()
        if (isLocationEnabled.value) {
            requestLocationUpdate()
        }
    }

    private fun getObjectDetectionList() = viewModelScope.launch {
        objectDetectionDbRepository.getAllObjectsWithDetails().collect { objectDetectionList ->
            _state.update { it.copy(detectedObjects = objectDetectionList) }
        }
    }

    fun requestLocationUpdate() = viewModelScope.launch {
        getLocationUseCase().collect { location ->
            _state.update { it.copy(currentLocation = location) }
        }
    }

    private fun updateLocationServiceStatus() {
        _isLocationEnabled.value = locationHelper.isConnected()
    }

    fun enableLocationRequest(
        context: Context,
        makeRequest: (intentSenderRequest: IntentSenderRequest) -> Unit,//Lambda to call when locations are off.
    ) {
        val locationRequest = LocationRequest.Builder( //Create a location request object
            Priority.PRIORITY_HIGH_ACCURACY, //Self explanatory
            10000 //Interval -> shorter the interval more frequent location updates
        ).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> =
            client.checkLocationSettings(builder.build()) //Checksettings with building a request
        task.addOnSuccessListener { locationSettingsResponse ->
            Timber.tag("Location")
                .d("enableLocationRequest: LocationService Already Enabled $locationSettingsResponse")
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution)
                            .build() //Create the request prompt
                    makeRequest(intentSenderRequest) //Make the request from UI
                } catch (sendEx: IntentSender.SendIntentException) {
                    _errorMessage.tryEmit(
                        "Something went wrong while requesting location permission: ${sendEx.message}"
                    )
                }
            }
        }
    }

    fun onMarkerClick(
        detectedObject: ObjectWithDetails
    ) {
        val imageFile = File(detectedObject.detectedObject.imagePath)
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(imageFile.path, options)
        val originalWidth = options.outWidth
        val originalHeight = options.outHeight

        _state.update { it.copy(
            imageFile = imageFile,
            originalImageWidth = originalWidth,
            originalImageHeight = originalHeight,
            selectedDetectedObject = detectedObject
        ) }
    }

    fun dismissImageDialog() {
        _state.update {
            it.copy(
                imageFile = null,
                originalImageWidth = null,
                originalImageHeight = null,
            )
        }
    }

    fun getObjectDetails(
        objectWithDetails: ObjectWithDetails,
        detectObjectsResponse: DetectObjectItem,
    ) = viewModelScope.launch {
        if (_state.value.isDialogLoading) return@launch

        _state.update { it.copy(isDialogLoading = true) }

        val objectDetailsByDetectedObjectId =
            objectDetectionDbRepository.getObjectDetailsByDetectedObjectIdList(objectWithDetails.detectedObject.id)
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

        val imageFile = File(objectWithDetails.detectedObject.imagePath)
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

                    // Save object details to database
                    response.data?.let { objectDetails ->
                        objectDetectionDbRepository.saveObjectDetailsWithRelatedData(
                            objectDetails = ObjectDetailsEntity(
                                detectedObjectId = objectWithDetails.detectedObject.id,
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
}