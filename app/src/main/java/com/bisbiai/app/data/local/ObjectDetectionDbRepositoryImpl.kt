package com.bisbiai.app.data.local

import com.bisbiai.app.data.local.dao.ObjectDetectionDao
import com.bisbiai.app.data.local.entity.DetectedObjectEntity
import com.bisbiai.app.data.local.entity.ExampleSentenceEntity
import com.bisbiai.app.data.local.entity.ObjectDetailsEntity
import com.bisbiai.app.data.local.entity.RelatedAdjectiveEntity
import com.bisbiai.app.data.local.relation.DetailsWithRelatedData
import com.bisbiai.app.data.local.relation.ObjectWithDetails
import com.bisbiai.app.domain.repository.ObjectDetectionDbRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObjectDetectionDbRepositoryImpl @Inject constructor(
    private val objectDetectionDao: ObjectDetectionDao
) : ObjectDetectionDbRepository {

    override suspend fun saveDetectedObject(detectedObject: DetectedObjectEntity): Long {
        return objectDetectionDao.insertDetectedObject(detectedObject)
    }

    override suspend fun saveObjectDetails(objectDetails: ObjectDetailsEntity): Long {
        return objectDetectionDao.insertObjectDetails(objectDetails)
    }

    override suspend fun saveRelatedAdjectives(relatedAdjectives: List<RelatedAdjectiveEntity>) {
        objectDetectionDao.insertRelatedAdjectives(relatedAdjectives)
    }

    override suspend fun saveExampleSentences(exampleSentences: List<ExampleSentenceEntity>) {
        objectDetectionDao.insertExampleSentences(exampleSentences)
    }

    override suspend fun saveObjectDetailsWithRelatedData(
        objectDetails: ObjectDetailsEntity,
        relatedAdjectives: List<RelatedAdjectiveEntity>,
        exampleSentences: List<ExampleSentenceEntity>
    ) {
        objectDetectionDao.insertObjectDetailsWithRelatedData(
            objectDetails, relatedAdjectives, exampleSentences
        )
    }

    override suspend fun saveCompleteObject(
        detectedObject: DetectedObjectEntity,
        objectDetails: ObjectDetailsEntity,
        relatedAdjectives: List<RelatedAdjectiveEntity>,
        exampleSentences: List<ExampleSentenceEntity>
    ) {
        objectDetectionDao.insertCompleteObject(
            detectedObject, objectDetails, relatedAdjectives, exampleSentences
        )
    }

    override suspend fun deleteDetectedObject(detectedObject: DetectedObjectEntity) {
        objectDetectionDao.deleteDetectedObject(detectedObject)
    }

    override suspend fun deleteDetectedObjectById(id: Long) {
        objectDetectionDao.deleteDetectedObjectById(id)
    }

    override suspend fun deleteAllDetectedObjects() {
        objectDetectionDao.deleteAllDetectedObjects()
    }

    override fun getAllObjectsWithDetails(): Flow<List<ObjectWithDetails>> {
        return objectDetectionDao.getAllObjectsWithDetails()
    }

    override fun getDetailsWithRelatedDataByObjectId(objectId: Long): Flow<DetailsWithRelatedData?> {
        return objectDetectionDao.getDetailsWithRelatedDataByObjectId(objectId)
    }

    override suspend fun getObjectDetailsByDetectedObjectIdList(detectedObjectId: Long): List<DetailsWithRelatedData> {
        return objectDetectionDao.getObjectDetailsByDetectedObjectIdList(detectedObjectId)
    }
}