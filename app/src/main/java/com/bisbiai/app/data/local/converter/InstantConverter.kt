package com.bisbiai.app.data.local.converter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class InstantConverter {
    @TypeConverter
    fun longToInstant(value: Long?): Instant? {
        return value?.let { Instant.fromEpochMilliseconds(it) }
    }

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? {
        return instant?.toEpochMilliseconds()
    }
}