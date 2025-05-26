// file: UserProgressRepository.kt
package com.bisbiai.app.data.local

import com.bisbiai.app.data.local.dao.AchievementDao
import com.bisbiai.app.data.local.dao.UserStatsDao
import com.bisbiai.app.data.local.entity.AchievementEntity
import com.bisbiai.app.data.local.entity.UserStatsEntity
import com.bisbiai.app.domain.repository.UserProgressRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProgressRepositoryImpl @Inject constructor(
    private val userStatsDao: UserStatsDao,
    private val achievementDao: AchievementDao,
    private val applicationScope: CoroutineScope // Untuk operasi background yang tidak terikat ViewModel
): UserProgressRepository {
    override val userStats: Flow<UserStatsEntity?> = userStatsDao.getUserStats()
    override val achievements: Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()

    // Event untuk achievement unlocked
    private val _achievementUnlockedEvent = MutableSharedFlow<AchievementEntity>(replay = 0) // replay = 0 agar hanya event baru yang diterima
    override val achievementUnlockedEvent: Flow<AchievementEntity> = _achievementUnlockedEvent.asSharedFlow()

    companion object {
        const val BASE_XP_PER_LEVEL_MULTIPLIER = 100 // XP untuk level N = N * MULTIPLIER
    }

    // Fungsi untuk memastikan UserStatsEntity ada
    override suspend fun ensureUserStatsExist() {
        if (userStatsDao.getUserStatsSnapshot() == null) {
            userStatsDao.insertOrUpdate(UserStatsEntity())
        }
    }


    override suspend fun addXp(amount: Int) = withContext(Dispatchers.IO) {
        var stats = userStatsDao.getUserStatsSnapshot() ?: UserStatsEntity().also { userStatsDao.insertOrUpdate(it) }
        var currentXp = stats.currentXp + amount
        var level = stats.level
        var xpToNextLevel = stats.xpToNextLevel

        while (currentXp >= xpToNextLevel) {
            level++
            currentXp -= xpToNextLevel
            xpToNextLevel = level * BASE_XP_PER_LEVEL_MULTIPLIER // Formula XP baru
        }
        userStatsDao.insertOrUpdate(stats.copy(level = level, currentXp = currentXp, xpToNextLevel = xpToNextLevel))
        checkAllAchievements() // Periksa achievement setelah XP bertambah
    }

    override suspend fun updateDayStreakOnLogin() = withContext(Dispatchers.IO) {
        var stats = userStatsDao.getUserStatsSnapshot() ?: UserStatsEntity().also { userStatsDao.insertOrUpdate(it) }
        val today = Calendar.getInstance()
        val lastLogin = stats.lastLoginDate?.let {
            Calendar.getInstance().apply { time = it }
        }

        var newStreak = stats.dayStreak
        if (lastLogin == null) { // Login pertama kali
            newStreak = 1
        } else {
            // Reset tahun, bulan, hari untuk perbandingan tanggal saja
            today.set(Calendar.HOUR_OF_DAY, 0); today.set(Calendar.MINUTE, 0); today.set(Calendar.SECOND, 0); today.set(Calendar.MILLISECOND, 0)
            lastLogin.set(Calendar.HOUR_OF_DAY, 0); lastLogin.set(Calendar.MINUTE, 0); lastLogin.set(Calendar.SECOND, 0); lastLogin.set(Calendar.MILLISECOND, 0)

            val diffMillis = today.timeInMillis - lastLogin.timeInMillis
            val diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis)

            if (diffDays == 1L) {
                newStreak++
            } else if (diffDays > 1L) {
                newStreak = 1 // Reset streak jika bolos
            }
            // Jika diffDays == 0, berarti login di hari yang sama, streak tidak berubah
        }
        userStatsDao.updateDayStreak(newStreak, Date()) // Update dengan tanggal hari ini
        checkAchievement("STREAK_3_DAYS", newStreak)
    }


    override suspend fun recordScan() = withContext(Dispatchers.IO) {
        userStatsDao.incrementTotalScans()
        val stats = userStatsDao.getUserStatsSnapshot() ?: return@withContext
        checkAchievement("FIRST_SCAN", stats.totalScans)
    }

    override suspend fun recordScenarioMastered() = withContext(Dispatchers.IO) {
        userStatsDao.incrementScenariosMastered()
        val stats = userStatsDao.getUserStatsSnapshot() ?: return@withContext
        checkAchievement("SCENARIO_ACE", stats.scenariosMastered)
        addXp(20) // XP untuk menyelesaikan skenario
    }

    override suspend fun recordPronunciationScore(score: Int) = withContext(Dispatchers.IO) {
        if (score > 80) {
            userStatsDao.incrementHighPronunciationScores()
            val stats = userStatsDao.getUserStatsSnapshot() ?: return@withContext
            checkAchievement("PRONUNCIATION_PRO", stats.highPronunciationScoresCount)
        }
        // XP berdasarkan skor, misal score/10
        addXp(score / 10)
    }

    override suspend fun recordWordsLearned(count: Int) = withContext(Dispatchers.IO) {
        userStatsDao.incrementWordsLearned(count)
        val stats = userStatsDao.getUserStatsSnapshot() ?: return@withContext
        checkAchievement("WORD_COLLECTOR_10", stats.wordsLearned)
    }

    private suspend fun checkAchievement(achievementId: String, currentProgress: Int) {
        val achievement = achievementDao.getAchievementById(achievementId)
        if (achievement != null && !achievement.isUnlocked && currentProgress >= achievement.requiredCount) {
            unlockAchievementInternal(achievement)
        }
    }

    // Untuk memeriksa semua achievement yang mungkin terpenuhi oleh perubahan stats umum
    private suspend fun checkAllAchievements() {
        val stats = userStatsDao.getUserStatsSnapshot() ?: return
        achievements.firstOrNull()?.forEach { achievement ->
            if (!achievement.isUnlocked) {
                val progress = when (achievement.id) {
                    "FIRST_SCAN" -> stats.totalScans
                    "SCENARIO_ACE" -> stats.scenariosMastered
                    "PRONUNCIATION_PRO" -> stats.highPronunciationScoresCount
                    "STREAK_3_DAYS" -> stats.dayStreak
                    "WORD_COLLECTOR_10" -> stats.wordsLearned
                    else -> 0
                }
                if (progress >= achievement.requiredCount) {
                    unlockAchievementInternal(achievement)
                }
            }
        }
    }


    private suspend fun unlockAchievementInternal(achievement: AchievementEntity) {
        val updatedRows = achievementDao.unlockAchievement(achievement.id, Date())
        if (updatedRows > 0) {
            addXp(achievement.xpReward)
            // Kirim event ke MutableSharedFlow
            _achievementUnlockedEvent.emit(achievement.copy(isUnlocked = true, unlockDate = Date())) // Kirim salinan yang sudah di-unlock
        }
    }
}