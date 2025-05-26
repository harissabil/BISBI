package com.bisbiai.app.data.local.converter

import androidx.room.TypeConverter
import com.bisbiai.app.data.remote.dto.GenerateLessonResponse
import com.google.gson.Gson

class LessonResponseConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromLessonResponse(lessonResponse: GenerateLessonResponse): String {
        return gson.toJson(lessonResponse)
    }

    @TypeConverter
    fun toLessonResponse(json: String): GenerateLessonResponse {
        val lessonResponse = gson.fromJson(json, GenerateLessonResponse::class.java)
        return lessonResponse
    }
}