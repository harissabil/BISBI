package com.bisbiai.app.domain.usecases

import com.bisbiai.app.data.location.ILocationService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val locationService: ILocationService
) {
    operator fun invoke(): Flow<LatLng?> = locationService.requestLocationUpdates()
}