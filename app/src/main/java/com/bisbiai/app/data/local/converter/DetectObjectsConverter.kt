package com.bisbiai.app.data.local.converter

import androidx.room.TypeConverter
import com.bisbiai.app.data.remote.dto.DetectObjectsResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DetectObjectsConverter {
    private val gson = Gson()
    
    @TypeConverter
    fun fromDetectedObjects(detectObjects: List<DetectObjectsResponse>): String {
        return gson.toJson(detectObjects)
    }

    @TypeConverter
    fun toDetectedObjects(json: String): List<DetectObjectsResponse> {
        val type = object : TypeToken<List<DetectObjectsResponse>>() {}.type
        return gson.fromJson(json, type)
    }
}