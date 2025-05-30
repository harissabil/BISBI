package com.bisbiai.app.ui.screen.flashcard

import com.bisbiai.app.domain.model.Flashcard

data class FlashcardState(
    val flashcards: List<Flashcard> = emptyList(),
    val currentFlashcardIndex: Int = 0,
)
