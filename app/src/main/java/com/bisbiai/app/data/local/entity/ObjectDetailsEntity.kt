package com.bisbiai.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.bisbiai.app.data.local.converter.BoundingBoxConverter
import com.bisbiai.app.data.remote.dto.BoundingBox

@Entity(
    tableName = "object_details",
    foreignKeys = [
        ForeignKey(
            entity = DetectedObjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["detectedObjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("detectedObjectId")]
)
data class ObjectDetailsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val detectedObjectId: Long,
    val objectNameEn: String,
    val objectNameId: String,
    val descriptionEn: String,
    val descriptionId: String,
    @TypeConverters(BoundingBoxConverter::class)
    val boundingBox: BoundingBox
)

@Entity(
    tableName = "related_adjectives",
    foreignKeys = [
        ForeignKey(
            entity = ObjectDetailsEntity::class,
            parentColumns = ["id"],
            childColumns = ["objectDetailsId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("objectDetailsId")]
)
data class RelatedAdjectiveEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val objectDetailsId: Long,
    val adjectiveEn: String,
    val adjectiveId: String
)

@Entity(
    tableName = "example_sentences",
    foreignKeys = [
        ForeignKey(
            entity = ObjectDetailsEntity::class,
            parentColumns = ["id"],
            childColumns = ["objectDetailsId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("objectDetailsId")]
)
data class ExampleSentenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val objectDetailsId: Long,
    val sentenceEn: String,
    val sentenceId: String
)