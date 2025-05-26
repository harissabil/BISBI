package com.bisbiai.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bisbiai.app.data.local.converter.DetectObjectsConverter
import com.bisbiai.app.data.local.converter.InstantConverter
import com.bisbiai.app.data.remote.dto.DetectObjectsResponse
import kotlinx.datetime.Instant

@Entity(tableName = "detected_objects")
data class DetectedObjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @TypeConverters(DetectObjectsConverter::class)
    val detectObjects: List<DetectObjectsResponse>,
    val imagePath: String,
    @TypeConverters(InstantConverter::class)
    val timestamp: Instant,
    val lat: Double,
    val long: Double
)