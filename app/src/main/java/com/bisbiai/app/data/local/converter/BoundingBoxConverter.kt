package com.bisbiai.app.data.local.converter

import androidx.room.TypeConverter
import com.bisbiai.app.data.remote.dto.BoundingBox
import com.google.gson.Gson

class BoundingBoxConverter {
    private val gson = Gson()
    
    @TypeConverter
    fun fromBoundingBox(boundingBox: BoundingBox): String {
        return gson.toJson(boundingBox)
    }
    
    @TypeConverter
    fun toBoundingBox(json: String): BoundingBox {
        return gson.fromJson(json, BoundingBox::class.java)
    }
}