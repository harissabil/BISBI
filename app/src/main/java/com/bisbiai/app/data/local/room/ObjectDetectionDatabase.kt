package com.bisbiai.app.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bisbiai.app.data.local.converter.BoundingBoxConverter
import com.bisbiai.app.data.local.converter.DateConverter
import com.bisbiai.app.data.local.converter.DetectObjectsConverter
import com.bisbiai.app.data.local.converter.InstantConverter
import com.bisbiai.app.data.local.converter.LessonResponseConverter
import com.bisbiai.app.data.local.dao.AchievementDao
import com.bisbiai.app.data.local.dao.ObjectDetectionDao
import com.bisbiai.app.data.local.dao.UserStatsDao
import com.bisbiai.app.data.local.entity.AchievementEntity
import com.bisbiai.app.data.local.entity.DetectedObjectEntity
import com.bisbiai.app.data.local.entity.ExampleSentenceEntity
import com.bisbiai.app.data.local.entity.ObjectDetailsEntity
import com.bisbiai.app.data.local.entity.RelatedAdjectiveEntity
import com.bisbiai.app.data.local.entity.ScenarioEntity
import com.bisbiai.app.data.local.entity.UserStatsEntity

@Database(
    entities = [
        DetectedObjectEntity::class,
        ObjectDetailsEntity::class,
        RelatedAdjectiveEntity::class,
        ExampleSentenceEntity::class,
        ScenarioEntity::class,
        UserStatsEntity::class,
        AchievementEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(
    DetectObjectsConverter::class,
    InstantConverter::class,
    BoundingBoxConverter::class,
    LessonResponseConverter::class,
    DateConverter::class
)
abstract class ObjectDetectionDatabase : RoomDatabase() {

    abstract fun objectDetectionDao(): ObjectDetectionDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun achievementDao(): AchievementDao
}