package com.bisbiai.app.data.local.converter

import androidx.room.TypeConverter
import com.bisbiai.app.data.remote.dto.DetectObjectsResponse
import com.google.gson.Gson

class DetectObjectsConverter {
    private val gson = Gson()
    
    @TypeConverter
    fun fromDetectedObjects(detectObjects: DetectObjectsResponse): String {
        return gson.toJson(detectObjects)
    }

    @TypeConverter
    fun toDetectedObjects(json: String): DetectObjectsResponse {
        val detectObjectsResponse = gson.fromJson(json, DetectObjectsResponse::class.java)
        return detectObjectsResponse
    }
}