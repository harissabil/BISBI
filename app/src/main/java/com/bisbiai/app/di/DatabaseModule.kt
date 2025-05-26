package com.bisbiai.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bisbiai.app.data.local.entity.AchievementEntity
import com.bisbiai.app.data.local.entity.UserStatsEntity
import com.bisbiai.app.data.local.room.ObjectDetectionDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ObjectDetectionDatabase =
        Room.databaseBuilder(
            context = context,
            klass = ObjectDetectionDatabase::class.java,
            name = "object_detection.db"
        )
            .fallbackToDestructiveMigration(false)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Populate data in a coroutine after database creation
                    CoroutineScope(Dispatchers.IO).launch {
                        val database = provideDatabase(context)

                        // Populate achievements
                        val achievementDao = database.achievementDao()
                        val initialAchievements = listOf(
                            AchievementEntity(
                                "FIRST_SCAN",
                                "First Scan",
                                "Completed your first image scan",
                                "\uD83D\uDD0D",
                                10,
                                requiredCount = 1
                            ),
                            AchievementEntity(
                                "SCENARIO_ACE",
                                "Scenario Ace",
                                "Mastered your first scenario",
                                "\uD83C\uDFAD",
                                20,
                                requiredCount = 1
                            ),
                            AchievementEntity(
                                "PRONUNCIATION_PRO",
                                "Pronunciation Pro",
                                "Scored over 80% on pronunciation",
                                "\uD83C\uDFA4",
                                25,
                                requiredCount = 1
                            ),
                            AchievementEntity(
                                "STREAK_3_DAYS",
                                "3-Day Streak",
                                "Used app for 3 days in a row",
                                "\uD83D\uDD25",
                                30,
                                requiredCount = 3
                            ),
                            AchievementEntity(
                                "WORD_COLLECTOR_10",
                                "Word Collector",
                                "Learned 10 new words",
                                "\uD83D\uDCDA",
                                15,
                                requiredCount = 10
                            )
                        )
                        achievementDao.insertAll(initialAchievements)

                        // Initialize UserStats if needed
                        val userStatsDao = database.userStatsDao()
                        if (userStatsDao.getUserStatsSnapshot() == null) {
                            userStatsDao.insertOrUpdate(UserStatsEntity())
                        }
                    }
                }
            })
            .build()

    @Provides
    @Singleton
    fun provideObjectDetectionDao(database: ObjectDetectionDatabase) =
        database.objectDetectionDao()

    @Provides
    @Singleton
    fun provideUserStatsDao(database: ObjectDetectionDatabase) =
        database.userStatsDao()

    @Provides
    @Singleton
    fun provideAchievementDao(database: ObjectDetectionDatabase) =
        database.achievementDao()

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}