package com.bisbiai.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bisbiai.app.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStatsEntity?>

    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getUserStatsSnapshot(): UserStatsEntity? // Untuk update langsung

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: UserStatsEntity)

    @Query("UPDATE user_stats SET currentXp = currentXp + :xpAmount WHERE id = 1")
    suspend fun addXp(xpAmount: Int)

    @Query("UPDATE user_stats SET level = :newLevel, currentXp = :newCurrentXp, xpToNextLevel = :newXpToNextLevel WHERE id = 1")
    suspend fun updateLevel(newLevel: Int, newCurrentXp: Int, newXpToNextLevel: Int)

    @Query("UPDATE user_stats SET dayStreak = :streak, lastLoginDate = :date WHERE id = 1")
    suspend fun updateDayStreak(streak: Int, date: Date)

    @Query("UPDATE user_stats SET totalScans = totalScans + 1 WHERE id = 1")
    suspend fun incrementTotalScans()

    @Query("UPDATE user_stats SET scenariosMastered = scenariosMastered + 1 WHERE id = 1")
    suspend fun incrementScenariosMastered()

    @Query("UPDATE user_stats SET highPronunciationScoresCount = highPronunciationScoresCount + 1 WHERE id = 1")
    suspend fun incrementHighPronunciationScores()

    @Query("UPDATE user_stats SET wordsLearned = wordsLearned + :count WHERE id = 1")
    suspend fun incrementWordsLearned(count: Int)
}