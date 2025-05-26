package com.bisbiai.app.ui.screen.scenarios

import com.bisbiai.app.data.local.entity.ScenarioEntity
import com.bisbiai.app.data.remote.dto.GenerateLessonResponse

data class ScenariosState(
    val isLoading: Boolean = false,
    val situationDescription: String = "",
    val scenarioHistory: List<ScenarioEntity> = emptyList(),

    val lessonData: GenerateLessonResponse? = null,
    val isGoingToDetail: Boolean = false,
)
