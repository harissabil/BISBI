package com.bisbiai.app.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MicNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.bisbiai.app.data.local.entity.AchievementEntity
import com.bisbiai.app.data.remote.dto.GenerateLessonResponse
import com.bisbiai.app.data.remote.dto.GetObjectDetailsResponse
import com.bisbiai.app.ui.UserProgressViewModel
import com.bisbiai.app.ui.components.AchievementUnlockedDialog
import com.bisbiai.app.ui.navigation.components.CustomNavigationBar
import com.bisbiai.app.ui.screen.auth.AuthScreen
import com.bisbiai.app.ui.screen.flashcard.FlashcardScreen
import com.bisbiai.app.ui.screen.home.HomeScreen
import com.bisbiai.app.ui.screen.profile.ProfileScreen
import com.bisbiai.app.ui.screen.scenario_detail.ScenarioDetailScreen
import com.bisbiai.app.ui.screen.scenarios.ScenariosScreen
import com.bisbiai.app.ui.screen.visual_lens.VisualLensScreen
import com.bisbiai.app.ui.screen.visual_lens_detail.VisualLensDetailScreen
import com.bisbiai.app.ui.screen.voice_gym.VoiceGymScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.json.Json
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    startDestination: Route,
) {
    val userProgressViewModel: UserProgressViewModel = hiltViewModel()

    var achievementToShowInDialog by remember { mutableStateOf<AchievementEntity?>(null) }

    // Observasi event achievement unlocked
    LaunchedEffect(key1 = Unit) { // atau key1 = userProgressViewModel jika Anda ingin restart jika ViewModel berubah
        userProgressViewModel.achievementUnlockedEvent.collectLatest { achievement ->
            // Tampilkan dialog, bukan snackbar
            achievementToShowInDialog = achievement
        }
    }

    // Tampilkan dialog jika achievementToShowInDialog tidak null
    achievementToShowInDialog?.let { achievement ->
        AchievementUnlockedDialog(
            achievement = achievement,
            onDismissRequest = { achievementToShowInDialog = null } // Tutup dialog
        )
    }

    val routes = listOf(
        Route.Auth,
        Route.Home,
        Route.VisualLens,
        Route.Scenarios,
        Route.VoiceGym,
        Route.Profile,
        Route.VisualLensDetail(""),
        Route.ScenarioDetail(""),
        Route.Flashcard,
    )
    val navigationBarItems = remember {
        listOf(
            NavigationItem(
                label = "Home",
                icon = Icons.Outlined.Home
            ),
            NavigationItem(
                label = "Visual Lens",
                icon = Icons.Outlined.CameraAlt
            ),
            NavigationItem(
                label = "Scenarios",
                icon = Icons.Outlined.ChatBubbleOutline
            ),
            NavigationItem(
                label = "Voice Gym",
                icon = Icons.Outlined.MicNone
            ),
            NavigationItem(
                label = "Profile",
                icon = Icons.Outlined.PersonOutline
            )
        )
    }

    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentDestination = navBackStackEntry?.destination
    var currentRoute by remember { mutableStateOf<Route?>(null) }
    routes.forEach { route ->
        if (currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true) {
            currentRoute = route
        }
    }
    val showBottomBar: (route: Route) -> Boolean = { route ->
        when (route) {
            Route.Auth -> false
            is Route.VisualLensDetail -> false
            is Route.ScenarioDetail -> false
            Route.Flashcard -> false
            else -> true
        }
    }

    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    selectedItem = when (currentRoute) {
        Route.Home -> 0
        Route.VisualLens -> 1
        Route.Scenarios -> 2
        Route.VoiceGym -> 3
        Route.Profile -> 4
        else -> selectedItem
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar(currentRoute ?: startDestination),
                enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it / 3 }) + fadeOut(),
            ) {
                CustomNavigationBar(
                    items = navigationBarItems,
                    selected = selectedItem,
                    onClick = {
                        when (it) {
                            0 -> navController.navigateToTab(Route.Home)
                            1 -> navController.navigateToTab(Route.VisualLens)
                            2 -> navController.navigateToTab(Route.Scenarios)
                            3 -> navController.navigateToTab(Route.VoiceGym)
                            4 -> navController.navigateToTab(Route.Profile)
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            modifier = modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            navController = navController,
            startDestination = startDestination,
        ) {
            composable<Route.Splash> {}

            composable<Route.Auth> {
                AuthScreen(
                    onNavigateToHomeScreen = {
                        navController.navigate(Route.Home) {
                            popUpTo(Route.Home) { inclusive = true }
                        }
                    }
                )
            }
            composable<Route.Home> {
                HomeScreen(
                    userProgressViewModel = userProgressViewModel,
                    onGoToObjectDetails = { objectDetails ->
                        // Convert object to JSON string
                        val objectDetailsJson = Json.encodeToString(objectDetails)
                        navController.navigate(Route.VisualLensDetail(objectDetailsJson))
                    },
                    onGoToFlashcard = {
                        navController.navigate(Route.Flashcard)
                    },
                )
            }
            composable<Route.Flashcard> {
                FlashcardScreen(
                    onNavigateUp = { navController.navigateUp() },
                )
            }
            composable<Route.VisualLens> {
                VisualLensScreen(
                    userProgressViewModel = userProgressViewModel,
                    onGoToObjectDetails = { objectDetails ->
                        // Convert object to JSON string
                        val objectDetailsJson = Json.encodeToString(objectDetails)
                        navController.navigate(Route.VisualLensDetail(objectDetailsJson))
                    }
                )
            }
            composable<Route.VisualLensDetail> {
                val args = it.toRoute<Route.VisualLensDetail>()
                // Convert JSON string back to object
                val objectDetails =
                    Json.decodeFromString<GetObjectDetailsResponse>(args.objectDetails)
                VisualLensDetailScreen(
                    userProgressViewModel = userProgressViewModel,
                    objectDetails = objectDetails,
                    onNavigateUp = { navController.navigateUp() }
                )
            }
            composable<Route.Scenarios> {
                ScenariosScreen(
                    userProgressViewModel = userProgressViewModel,
                    onGoToLessonDetail = { detail ->
                        val lessonDetailJson = Json.encodeToString(detail)
                        navController.navigate(Route.ScenarioDetail(lessonDetailJson))
                    }
                )
            }
            composable<Route.ScenarioDetail> {
                val args = it.toRoute<Route.ScenarioDetail>()
                // Convert JSON string back to object
                val lessonDetail =
                    Json.decodeFromString<GenerateLessonResponse>(args.lessonData)
                ScenarioDetailScreen(
                    userProgressViewModel = userProgressViewModel,
                    lessonData = lessonDetail,
                    onNavigateUp = { navController.navigateUp() },
                )
            }
            composable<Route.VoiceGym> {
                VoiceGymScreen(
                    userProgressViewModel = userProgressViewModel
                )
            }
            composable<Route.Profile> {
                ProfileScreen(
                    onNavigateToAuthScreen = {
                        navController.navigate(Route.Auth) {
                            popUpTo(Route.Auth) { inclusive = true }
                        }
                    },
                    userProgressViewModel = userProgressViewModel
                )
            }
        }
    }
}

private fun NavController.navigateToTab(route: Route) {
    this.navigate(route) {
        popUpTo(this@navigateToTab.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}