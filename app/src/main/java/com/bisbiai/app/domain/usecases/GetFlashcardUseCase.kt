package com.bisbiai.app.domain.usecases

import com.bisbiai.app.domain.model.Flashcard
import com.bisbiai.app.domain.repository.ObjectDetectionDbRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetFlashcardUseCase @Inject constructor(
    private val objectDetectionDbRepository: ObjectDetectionDbRepository,
) {
    suspend operator fun invoke(): List<Flashcard> {
        val objectsWithDetails =
            objectDetectionDbRepository.getAllDetectedObjectDetails().map { detectedObjectDetail ->
                Flashcard(
                    objectName = detectedObjectDetail.objectNameEn,
                    translation = detectedObjectDetail.objectNameId,
                )
            }
        val scenarios = objectDetectionDbRepository.getAllScenarios().first()
        val phrases = scenarios.flatMap { scenarioEntity ->
                scenarioEntity.lessonData.keyPhrases.map { phrase ->
                    Flashcard(
                        objectName = phrase.phrase.en,
                        translation = phrase.phrase.id
                    )
                }
            }
        val terms = scenarios.flatMap { scenarioEntity ->
                scenarioEntity.lessonData.vocabulary.map { term ->
                    Flashcard(
                        objectName = term.term.en,
                        translation = term.term.id
                    )
                }
            }

        val uniqueFlashcards = (objectsWithDetails + phrases + terms).distinctBy { it.objectName }
        val randomizedFlashcards = uniqueFlashcards.shuffled()

        return randomizedFlashcards
    }
}