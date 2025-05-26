package com.bisbiai.app.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.bisbiai.app.data.local.entity.DetectedObjectEntity
import com.bisbiai.app.data.local.entity.ExampleSentenceEntity
import com.bisbiai.app.data.local.entity.ObjectDetailsEntity
import com.bisbiai.app.data.local.entity.RelatedAdjectiveEntity

data class ObjectWithDetails(
    @Embedded val detectedObject: DetectedObjectEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "detectedObjectId"
    )
    val objectDetails: List<ObjectDetailsEntity>?
)

data class DetailsWithRelatedData(
    @Embedded val objectDetails: ObjectDetailsEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "objectDetailsId"
    )
    val relatedAdjectives: List<RelatedAdjectiveEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "objectDetailsId"
    )
    val exampleSentences: List<ExampleSentenceEntity>
)