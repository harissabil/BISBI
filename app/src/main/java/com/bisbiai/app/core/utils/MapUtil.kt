package com.bisbiai.app.core.utils

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import coil.Coil
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun CameraPositionState.centerOnLocation(
    location: LatLng,
) = animate(
    update = CameraUpdateFactory.newLatLngZoom(location, 17.5f),
    durationMs = 500
)

fun getExpandedBounds(currentLocationState: LatLng, expansionFactor: Double = 0.005): LatLngBounds {
    // Adjust the expansion factor according to your needs
    val southwest = LatLng(
        currentLocationState.latitude - expansionFactor,
        currentLocationState.longitude - expansionFactor
    )
    val northeast = LatLng(
        currentLocationState.latitude + expansionFactor,
        currentLocationState.longitude + expansionFactor
    )
    return LatLngBounds.Builder()
        .include(southwest)
        .include(northeast)
        .build()
}

suspend fun loadBitmapDescriptorFromUrl(context: Context, imageUrl: String): BitmapDescriptor {
    val loader = Coil.imageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .transformations(CircleCropTransformation())
        .size(130, 130)
        .build()
    val result = withContext(Dispatchers.IO) { loader.execute(request) }
    val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
        ?: throw IllegalArgumentException("Unable to load image")
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}