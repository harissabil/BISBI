package com.bisbiai.app.data.mapper

import com.bisbiai.app.data.local.entity.ExampleSentenceEntity
import com.bisbiai.app.data.local.entity.ObjectDetailsEntity
import com.bisbiai.app.data.local.entity.RelatedAdjectiveEntity
import com.bisbiai.app.data.local.relation.DetailsWithRelatedData
import com.bisbiai.app.data.remote.dto.BoundingBox
import com.bisbiai.app.data.remote.dto.Description
import com.bisbiai.app.data.remote.dto.ExampleSentencesItem
import com.bisbiai.app.data.remote.dto.GetObjectDetailsResponse
import com.bisbiai.app.data.remote.dto.ObjectName
import com.bisbiai.app.data.remote.dto.RelatedAdjectivesItem

// Entity to Response mappings
fun DetailsWithRelatedData.toGetObjectDetailsResponse(): GetObjectDetailsResponse {
    return GetObjectDetailsResponse(
        objectName = ObjectName(
            en = objectDetails.objectNameEn,
            id = objectDetails.objectNameId
        ),
        description = Description(
            en = objectDetails.descriptionEn,
            id = objectDetails.descriptionId
        ),
        relatedAdjectives = relatedAdjectives.map { it.toRelatedAdjectivesItem() },
        exampleSentences = exampleSentences.map { it.toExampleSentencesItem() }
    )
}

fun RelatedAdjectiveEntity.toRelatedAdjectivesItem(): RelatedAdjectivesItem {
    return RelatedAdjectivesItem(
        en = adjectiveEn,
        id = adjectiveId
    )
}

fun ExampleSentenceEntity.toExampleSentencesItem(): ExampleSentencesItem {
    return ExampleSentencesItem(
        en = sentenceEn,
        id = sentenceId
    )
}

// Response to Entity mappings (for completeness)
fun GetObjectDetailsResponse.toEntities(detectedObjectId: Long, boundingBox: BoundingBox): Triple<ObjectDetailsEntity, List<RelatedAdjectiveEntity>, List<ExampleSentenceEntity>> {
    val objectDetails = ObjectDetailsEntity(
        detectedObjectId = detectedObjectId,
        objectNameEn = objectName.en,
        objectNameId = objectName.id,
        descriptionEn = description.en,
        descriptionId = description.id,
        boundingBox = boundingBox
    )
    
    val relatedAdjectiveEntities = relatedAdjectives.map {
        RelatedAdjectiveEntity(
            objectDetailsId = 0, // Will be set after insertion
            adjectiveEn = it.en,
            adjectiveId = it.id
        )
    }
    
    val exampleSentenceEntities = exampleSentences.map {
        ExampleSentenceEntity(
            objectDetailsId = 0, // Will be set after insertion
            sentenceEn = it.en,
            sentenceId = it.id
        )
    }
    
    return Triple(objectDetails, relatedAdjectiveEntities, exampleSentenceEntities)
}