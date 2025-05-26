package com.bisbiai.app.data.location.current_location

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
}