package com.bisbiai.app.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    @Serializable
    data object Splash : Route()

    @Serializable
    data object Auth : Route()

    @Serializable
    data object Home : Route()

    @Serializable
    data object VisualLens : Route()

    @Serializable
    data class VisualLensDetail(
        val objectDetails: String
    ) : Route()

    @Serializable
    data object Scenarios : Route()

    @Serializable
    data object VoiceGym : Route()

    @Serializable
    data object Profile : Route()
}