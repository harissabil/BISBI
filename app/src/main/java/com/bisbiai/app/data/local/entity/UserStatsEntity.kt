package com.bisbiai.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bisbiai.app.data.local.converter.BoundingBoxConverter
import com.bisbiai.app.data.local.converter.DateConverter
import java.util.Date

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1, // Hanya ada satu baris untuk pengguna saat ini
    val level: Int = 1,
    val currentXp: Int = 0,
    val xpToNextLevel: Int = 100, // XP awal untuk naik ke level 2
    val dayStreak: Int = 0,
    @TypeConverters(DateConverter::class)
    val lastLoginDate: Date? = null, // Untuk menghitung day streak

    // Atribut untuk tracking achievement
    val totalScans: Int = 0,
    val scenariosMastered: Int = 0,
    val highPronunciationScoresCount: Int = 0, // Jumlah skor > 80%
    val wordsLearned: Int = 0
)