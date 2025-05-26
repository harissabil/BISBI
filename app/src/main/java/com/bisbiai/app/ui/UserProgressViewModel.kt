package com.bisbiai.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bisbiai.app.data.local.entity.AchievementEntity
import com.bisbiai.app.data.local.entity.UserStatsEntity
import com.bisbiai.app.domain.repository.UserProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProgressViewModel @Inject constructor(
    private val userProgressRepository: UserProgressRepository
) : ViewModel() {

    val userStats: StateFlow<UserStatsEntity?> =
        userProgressRepository.userStats.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = null // Atau UserStatsEntity() jika ingin default
        )

    val achievements: StateFlow<List<AchievementEntity>> =
        userProgressRepository.achievements.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Untuk notifikasi achievement (opsional)
    private val _achievementUnlockedEvent = MutableSharedFlow<AchievementEntity>()
    val achievementUnlockedEvent = _achievementUnlockedEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            userProgressRepository.ensureUserStatsExist() // Pastikan data user ada
            userProgressRepository.updateDayStreakOnLogin() // Cek streak saat ViewModel dibuat
        }

        // Kumpulkan event dari repository dan emit ulang melalui SharedFlow ViewModel
        userProgressRepository.achievementUnlockedEvent
            .onEach { achievement ->
                _achievementUnlockedEvent.emit(achievement)
            }
            .launchIn(viewModelScope) // Gunakan launchIn untuk mengoleksi Flow di ViewModelScope
    }

    fun addExperience(xp: Int) {
        viewModelScope.launch {
            userProgressRepository.addXp(xp)
        }
    }

    fun onVisualScanCompleted() {
        viewModelScope.launch {
            userProgressRepository.recordScan()
            // Tambahkan XP untuk scan
            userProgressRepository.addXp(5) // Contoh XP untuk scan
        }
    }

    fun onScenarioMastered() {
        viewModelScope.launch {
            userProgressRepository.recordScenarioMastered()
            // XP sudah ditambahkan di dalam recordScenarioMastered di repository
        }
    }

    fun onPronunciationScored(score: Int) {
        viewModelScope.launch {
            userProgressRepository.recordPronunciationScore(score)
            // XP sudah ditambahkan di dalam recordPronunciationScore di repository
        }
    }

    fun onWordsLearnedFromScenario() {
        viewModelScope.launch {
            userProgressRepository.recordWordsLearned(1)
            // Tambahkan XP untuk kata yang dipelajari
            userProgressRepository.addXp(5) // Contoh 2 XP per kata
        }
    }
}