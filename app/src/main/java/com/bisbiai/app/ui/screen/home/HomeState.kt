package com.bisbiai.app.ui.screen.home

import com.bisbiai.app.data.local.relation.ObjectWithDetails
import com.bisbiai.app.data.remote.dto.GetObjectDetailsResponse
import com.google.android.gms.maps.model.LatLng
import java.io.File

data class HomeState(
    val currentLocation: LatLng? = null,
    val detectedObjects: List<ObjectWithDetails> = emptyList(),
    val selectedDetectedObject: ObjectWithDetails? = null,
    val isDialogLoading: Boolean = false,
    val objectDetails:  GetObjectDetailsResponse? = null,
    val isGoingToDetails: Boolean = false,
    val imageFile: File? = null,
    val originalImageWidth: Int? = null,
    val originalImageHeight: Int? = null,
)
