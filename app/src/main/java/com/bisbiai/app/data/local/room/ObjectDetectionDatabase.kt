package com.bisbiai.app.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bisbiai.app.data.local.converter.BoundingBoxConverter
import com.bisbiai.app.data.local.converter.DetectObjectsConverter
import com.bisbiai.app.data.local.converter.InstantConverter
import com.bisbiai.app.data.local.dao.ObjectDetectionDao
import com.bisbiai.app.data.local.entity.DetectedObjectEntity
import com.bisbiai.app.data.local.entity.ExampleSentenceEntity
import com.bisbiai.app.data.local.entity.ObjectDetailsEntity
import com.bisbiai.app.data.local.entity.RelatedAdjectiveEntity

@Database(
    entities = [
        DetectedObjectEntity::class,
        ObjectDetailsEntity::class,
        RelatedAdjectiveEntity::class,
        ExampleSentenceEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DetectObjectsConverter::class, InstantConverter::class, BoundingBoxConverter::class)
abstract class ObjectDetectionDatabase : RoomDatabase() {

    abstract fun objectDetectionDao(): ObjectDetectionDao
}