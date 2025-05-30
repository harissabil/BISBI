package com.bisbiai.app.domain.repository

import com.bisbiai.app.data.local.entity.DetectedObjectEntity
import com.bisbiai.app.data.local.entity.ExampleSentenceEntity
import com.bisbiai.app.data.local.entity.ObjectDetailsEntity
import com.bisbiai.app.data.local.entity.RelatedAdjectiveEntity
import com.bisbiai.app.data.local.entity.ScenarioEntity
import com.bisbiai.app.data.local.relation.DetailsWithRelatedData
import com.bisbiai.app.data.local.relation.ObjectWithDetails
import kotlinx.coroutines.flow.Flow

interface ObjectDetectionDbRepository {
    // Save operations
    suspend fun saveDetectedObject(detectedObject: DetectedObjectEntity): Long
    suspend fun saveObjectDetails(objectDetails: ObjectDetailsEntity): Long
    suspend fun saveScenario(scenarioEntity: ScenarioEntity): Long
    suspend fun saveRelatedAdjectives(relatedAdjectives: List<RelatedAdjectiveEntity>)
    suspend fun saveExampleSentences(exampleSentences: List<ExampleSentenceEntity>)

    // Transaction operations
    suspend fun saveObjectDetailsWithRelatedData(
        objectDetails: ObjectDetailsEntity,
        relatedAdjectives: List<RelatedAdjectiveEntity>,
        exampleSentences: List<ExampleSentenceEntity>
    )

    suspend fun saveCompleteObject(
        detectedObject: DetectedObjectEntity,
        objectDetails: ObjectDetailsEntity,
        relatedAdjectives: List<RelatedAdjectiveEntity>,
        exampleSentences: List<ExampleSentenceEntity>
    )

    // Delete operations
    suspend fun deleteDetectedObject(detectedObject: DetectedObjectEntity)
    suspend fun deleteDetectedObjectById(id: Long)
    suspend fun deleteAllDetectedObjects()

    // Query operations
    fun getAllObjectsWithDetails(): Flow<List<ObjectWithDetails>>
    suspend fun getAllDetectedObjectDetails(): List<ObjectDetailsEntity>
    fun getDetailsWithRelatedDataByObjectId(objectId: Long): Flow<DetailsWithRelatedData?>
    fun getAllScenarios(): Flow<List<ScenarioEntity>>
    suspend fun getScenarioById(id: Long): ScenarioEntity?

    // Get object details by detected object ID as a list (not real-time)
    suspend fun getObjectDetailsByDetectedObjectIdList(detectedObjectId: Long): List<DetailsWithRelatedData>
}