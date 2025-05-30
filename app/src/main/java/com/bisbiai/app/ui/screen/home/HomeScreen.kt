package com.bisbiai.app.ui.screen.home

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bisbiai.app.R
import com.bisbiai.app.core.utils.centerOnLocation
import com.bisbiai.app.core.utils.getExpandedBounds
import com.bisbiai.app.data.remote.dto.GetObjectDetailsResponse
import com.bisbiai.app.ui.UserProgressViewModel
import com.bisbiai.app.ui.screen.home.components.CustomMapMarker
import com.bisbiai.app.ui.screen.home.components.ExpandableImageSelector
import com.bisbiai.app.ui.screen.home.components.ExpandableUserStatsCard
import com.bisbiai.app.ui.screen.visual_lens.components.ObjectDetectionResultsDialog
import com.bisbiai.app.ui.theme.spacing
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import top.yukonga.miuix.kmp.basic.Scaffold

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    userProgressViewModel: UserProgressViewModel,
    onGoToObjectDetails: (GetObjectDetailsResponse) -> Unit,
    onGoToFlashcard: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val progressData by userProgressViewModel.userStats.collectAsStateWithLifecycle() // Data dari UserProgressViewModel
    val isLocationEnabled by viewModel.isLocationEnabled.collectAsState()
    val locationRequestLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                // User has enabled location
                Timber.d("Location enabled")
                viewModel.requestLocationUpdate()
            } else {
                if (!isLocationEnabled) {
                    // If the user cancels, still make a check and then give a snackbar
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        val result = snackbarHostState
                            .showSnackbar(
                                message = "Location is required to get your current location",
                                actionLabel = "Enable",
                                duration = SnackbarDuration.Long
                            )
                        when (result) {
                            SnackbarResult.ActionPerformed -> {
                                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                context.startActivity(intent)
                            }

                            SnackbarResult.Dismissed -> {
                                Timber.d("Snackbar dismissed")
                            }
                        }
                    }
                }
            }
        }

    LaunchedEffect(key1 = state.isGoingToDetails) {
        if (state.isGoingToDetails && state.objectDetails != null) {
            state.objectDetails?.let { details ->
                onGoToObjectDetails(details)
                viewModel.resetObjectDetails() // Reset state after navigating
            }
        }
    }

    LaunchedEffect(key1 = isLocationEnabled) {
        if (!isLocationEnabled) {
            viewModel.enableLocationRequest(context = context) {
                locationRequestLauncher.launch(it)
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.errorMessage.collectLatest { message ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message = message)
        }
    }

    val cameraPositionState = rememberCameraPositionState()
    val properties = MapProperties(
        mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style),
        isBuildingEnabled = true,
        isMyLocationEnabled = true,
    )

    LaunchedEffect(key1 = Unit) {
        if (cameraPositionState.position.tilt != 90f) {
            cameraPositionState.position = CameraPosition.builder()
                .target(cameraPositionState.position.target)
                .tilt(90f)
                .build()
        }
        if (state.currentLocation != null) {
            cameraPositionState.centerOnLocation(state.currentLocation!!)
        }
    }

    val firstOnly = remember { mutableStateOf(false) }
    LaunchedEffect(key1 = state.currentLocation) {
        if (state.currentLocation != null && cameraPositionState.position.zoom < 17.5f) {
            if (cameraPositionState.position.target !in getExpandedBounds(
                    state.currentLocation!!,
                    0.01
                )
            ) {
                if (!firstOnly.value) {
                    firstOnly.value = true
                    cameraPositionState.centerOnLocation(state.currentLocation!!)
                }
            }
        }
    }

    val uiSettings = MapUiSettings(
        zoomControlsEnabled = false,
        compassEnabled = false,
        myLocationButtonEnabled = false,
    )

    // State for the expandable selector
    var isImageSelectorExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
//        topBar = {
//            BaseTopAppBar(title = "BISBI AI")
//        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            val contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(layoutDirection = LocalLayoutDirection.current),
                top = innerPadding.calculateTopPadding(),
                end = innerPadding.calculateEndPadding(layoutDirection = LocalLayoutDirection.current),
                bottom = 0.dp,
            )

            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                cameraPositionState = cameraPositionState,
                contentPadding = contentPadding,
                properties = properties,
                uiSettings = uiSettings,
            ) {
                state.detectedObjects.onEach { detectedObject ->
                    CustomMapMarker(
                        imagePath = detectedObject.detectedObject.imagePath,
                        fullName = detectedObject.detectedObject.id.toString(),
                        location = LatLng(
                            detectedObject.detectedObject.lat,
                            detectedObject.detectedObject.long
                        ),
                        onClick = {
                            viewModel.onMarkerClick(detectedObject)
                        }
                    )
                }
            }

            ExpandableUserStatsCard(
                modifier = modifier
                    .padding(
                        horizontal = MaterialTheme.spacing.medium,
                        vertical = MaterialTheme.spacing.medium
                    )
                    .padding(top = innerPadding.calculateTopPadding()),
                currentLevel = progressData?.level ?: 1,
                currentXp = progressData?.currentXp ?: 0,
                xpToNextLevel = progressData?.xpToNextLevel ?: 100,
                dayStreak = progressData?.dayStreak ?: 0,
            )

            // Add the ExpandableImageSelector here
            ExpandableImageSelector(
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Align to bottom right
                    .padding(
                        bottom = innerPadding.calculateBottomPadding() + 16.dp, // Add some padding from bottom, respecting scaffold padding
                        end = 16.dp // Padding from the right edge
                    ),
                isExpanded = isImageSelectorExpanded,
                onToggle = { isImageSelectorExpanded = !isImageSelectorExpanded },
                items = state.detectedObjects, // Pass your list of items
                onItemClick = { selectedItem ->
                    scope.launch {
                        cameraPositionState.centerOnLocation(
                            LatLng(
                                selectedItem.detectedObject.lat,
                                selectedItem.detectedObject.long
                            )
                        )
                    }
                },
                onFlashcardClick = onGoToFlashcard
            )

            val showDetectionDialog = state.imageFile != null &&
                    state.detectedObjects != null &&
                    state.originalImageWidth != null &&
                    state.originalImageHeight != null &&
                    state.selectedDetectedObject != null

            if (showDetectionDialog) {
                ObjectDetectionResultsDialog(
                    imageFile = state.imageFile!!,
                    detectionResult = state.selectedDetectedObject!!.detectedObject.detectObjects,
                    originalImageWidth = state.originalImageWidth!!,
                    originalImageHeight = state.originalImageHeight!!,
                    isLoading = state.isDialogLoading, // Pass loading state specific to detection
                    onDismissRequest = { viewModel.dismissImageDialog() },
                    onObjectClick = {
                        viewModel.getObjectDetails(
                            objectWithDetails = state.selectedDetectedObject!!,
                            detectObjectsResponse = it
                        )
                        userProgressViewModel.addExperience(5) // Add experience when an object is clicked
                    }
                )
            }
        }
    }
}