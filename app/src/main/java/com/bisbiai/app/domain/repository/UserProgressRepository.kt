// file: UserProgressRepository.kt
package com.bisbiai.app.domain.repository

import com.bisbiai.app.data.local.entity.AchievementEntity
import com.bisbiai.app.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

interface UserProgressRepository  {
    val userStats: Flow<UserStatsEntity?>
    val achievements: Flow<List<AchievementEntity>>
    val achievementUnlockedEvent: Flow<AchievementEntity>

    // Fungsi untuk memastikan UserStatsEntity ada
    suspend fun ensureUserStatsExist()
    suspend fun addXp(amount: Int)
    suspend fun updateDayStreakOnLogin()


    suspend fun recordScan()

    suspend fun recordScenarioMastered()

    suspend fun recordPronunciationScore(score: Int)

    suspend fun recordWordsLearned(count: Int)
}