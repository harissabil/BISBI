package com.bisbiai.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bisbiai.app.data.local.converter.DateConverter
import java.util.Date

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String, // ID unik, e.g., "FIRST_SCAN"
    val name: String,
    val description: String,
    val icon: String, // Nama resource ikon (opsional)
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    @TypeConverters(DateConverter::class)
    val unlockDate: Date? = null,
    val requiredCount: Int = 1 // Jumlah yang dibutuhkan untuk unlock (misal, 3 untuk 3-Day Streak)
)