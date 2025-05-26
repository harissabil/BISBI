package com.bisbiai.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bisbiai.app.data.local.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE id = :achievementId")
    suspend fun getAchievementById(achievementId: String): AchievementEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE) // Ignore jika sudah ada, karena kita pre-populate
    suspend fun insertAll(achievements: List<AchievementEntity>)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockDate = :date WHERE id = :achievementId AND isUnlocked = 0")
    suspend fun unlockAchievement(
        achievementId: String,
        date: Date,
    ): Int // Mengembalikan jumlah baris yang diupdate
}