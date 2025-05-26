package com.bisbiai.app.ui.screen.visual_lens

import com.bisbiai.app.data.local.relation.ObjectWithDetails
import com.bisbiai.app.data.remote.dto.DetectObjectsResponse
import com.bisbiai.app.data.remote.dto.GetObjectDetailsResponse
import java.io.File

data class VisualLensState(
    val isLoading: Boolean = false,
    val isDialogLoading: Boolean = false,
    val imageFile: File? = null,
    val originalImageWidth: Int? = null,
    val originalImageHeight: Int? = null,
    val detectedObjects: List<DetectObjectsResponse>? = null,
    val objectDetails: GetObjectDetailsResponse? = null,
    val isGoingToDetails: Boolean = false,
    val lat: Double? = null,
    val long: Double? = null,
    val detectedObjectId: Long? = null,

    val histories: List<ObjectWithDetails> = emptyList()
)
