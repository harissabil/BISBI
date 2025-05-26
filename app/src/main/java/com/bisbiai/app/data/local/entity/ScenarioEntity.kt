package com.bisbiai.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bisbiai.app.data.local.converter.InstantConverter
import com.bisbiai.app.data.local.converter.LessonResponseConverter
import com.bisbiai.app.data.remote.dto.GenerateLessonResponse
import kotlinx.datetime.Instant

@Entity(tableName = "scenario")
data class ScenarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @TypeConverters(LessonResponseConverter::class)
    val lessonData: GenerateLessonResponse,
    @TypeConverters(InstantConverter::class)
    val timestamp: Instant,
)
