package com.bisbiai.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.bisbiai.app.data.local.entity.DetectedObjectEntity
import com.bisbiai.app.data.local.entity.ExampleSentenceEntity
import com.bisbiai.app.data.local.entity.ObjectDetailsEntity
import com.bisbiai.app.data.local.entity.RelatedAdjectiveEntity
import com.bisbiai.app.data.local.entity.ScenarioEntity
import com.bisbiai.app.data.local.relation.DetailsWithRelatedData
import com.bisbiai.app.data.local.relation.ObjectWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ObjectDetectionDao {
    // Insert methods for detected objects
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetectedObject(detectedObject: DetectedObjectEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObjectDetails(objectDetails: ObjectDetailsEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelatedAdjectives(relatedAdjectives: List<RelatedAdjectiveEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExampleSentences(exampleSentences: List<ExampleSentenceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenario(scenarioEntity: ScenarioEntity): Long
    
    // Transaction to save object details with its related data
    @Transaction
    suspend fun insertObjectDetailsWithRelatedData(
        objectDetails: ObjectDetailsEntity,
        relatedAdjectives: List<RelatedAdjectiveEntity>,
        exampleSentences: List<ExampleSentenceEntity>
    ) {
        val objectDetailsId = insertObjectDetails(objectDetails)
        
        val updatedAdjectives = relatedAdjectives.map { 
            it.copy(objectDetailsId = objectDetailsId) 
        }
        insertRelatedAdjectives(updatedAdjectives)
        
        val updatedSentences = exampleSentences.map { 
            it.copy(objectDetailsId = objectDetailsId) 
        }
        insertExampleSentences(updatedSentences)
    }
    
    // Transaction to save complete object with all related data
    @Transaction
    suspend fun insertCompleteObject(
        detectedObject: DetectedObjectEntity,
        objectDetails: ObjectDetailsEntity,
        relatedAdjectives: List<RelatedAdjectiveEntity>,
        exampleSentences: List<ExampleSentenceEntity>
    ) {
        val detectedObjectId = insertDetectedObject(detectedObject)
        
        val updatedDetails = objectDetails.copy(detectedObjectId = detectedObjectId)
        val objectDetailsId = insertObjectDetails(updatedDetails)
        
        val updatedAdjectives = relatedAdjectives.map { 
            it.copy(objectDetailsId = objectDetailsId) 
        }
        insertRelatedAdjectives(updatedAdjectives)
        
        val updatedSentences = exampleSentences.map { 
            it.copy(objectDetailsId = objectDetailsId) 
        }
        insertExampleSentences(updatedSentences)
    }
    
    // Delete methods
    @Delete
    suspend fun deleteDetectedObject(detectedObject: DetectedObjectEntity)
    
    @Query("DELETE FROM detected_objects WHERE id = :id")
    suspend fun deleteDetectedObjectById(id: Long)
    
    @Query("DELETE FROM detected_objects")
    suspend fun deleteAllDetectedObjects()
    
    // Get all objects with Flow (realtime)
    @Transaction
    @Query("SELECT * FROM detected_objects ORDER BY timestamp DESC")
    fun getAllObjectsWithDetails(): Flow<List<ObjectWithDetails>>
    
    @Transaction
    @Query("SELECT * FROM object_details WHERE detectedObjectId = :objectId")
    fun getDetailsWithRelatedDataByObjectId(objectId: Long): Flow<DetailsWithRelatedData?>

    @Query("SELECT * FROM scenario ORDER BY timestamp DESC")
    fun getAllScenarios(): Flow<List<ScenarioEntity>>

    @Query("SELECT * FROM scenario WHERE id = :id")
    suspend fun getScenarioById(id: Long): ScenarioEntity?

    // Get object details by detected object ID as a list (not real-time)
    @Query("SELECT * FROM object_details WHERE detectedObjectId = :detectedObjectId")
    suspend fun getObjectDetailsByDetectedObjectIdList(detectedObjectId: Long): List<DetailsWithRelatedData>
}