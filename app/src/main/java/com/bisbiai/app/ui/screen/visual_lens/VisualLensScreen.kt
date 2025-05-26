package com.bisbiai.app.ui.screen.visual_lens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bisbiai.app.R
import com.bisbiai.app.data.remote.dto.GetObjectDetailsResponse
import com.bisbiai.app.ui.components.FullScreenLoading
import com.bisbiai.app.ui.screen.visual_lens.components.HistoryBottomSheet
import com.bisbiai.app.ui.screen.visual_lens.components.ObjectDetectionResultsDialog
import kotlinx.coroutines.flow.collectLatest
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonColors
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualLensScreen(
    modifier: Modifier = Modifier,
    cameraPreviewViewModel: CameraPreviewViewModel = hiltViewModel(),
    visualLensViewModel: VisualLensViewModel = hiltViewModel(),
    onGoToObjectDetails: (GetObjectDetailsResponse) -> Unit
) {
    val context = LocalContext.current
    val state by visualLensViewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        visualLensViewModel.errorMessage.collectLatest { message ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message = message)
        }
    }

    LaunchedEffect(key1 = state.isGoingToDetails) {
        if (state.isGoingToDetails && state.objectDetails != null) {
            state.objectDetails?.let { details ->
                onGoToObjectDetails(details)
                visualLensViewModel.resetObjectDetails() // Reset state after navigating
            }
        }
    }

    var isHistoryBottomSheetVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreviewScreen(
                modifier = modifier.fillMaxSize(),
                viewModel = cameraPreviewViewModel,
                lifecycleOwner = LocalLifecycleOwner.current,
            )

            // Instructional Text
            Text(
                text = "Scan objects around you to learn their names and details in English.",
                color = Color.White, // Ensures text is visible on a darkish scrim
                textAlign = TextAlign.Center,
                style = MiuixTheme.textStyles.body2.copy( // Use Miuix typography
                    lineHeight = 18.sp // Adjust line height for better readability if text wraps
                ),
                modifier = Modifier
                    .fillMaxWidth() // Text container takes available width within its padding
                    .background(
                        // Use a semi-transparent background from your theme or a default dark scrim
                        // This assumes MiuixTheme.colorScheme.background is dark enough when alpha is applied,
                        // or it's a color intended for such overlays.
                        // Making it slightly more opaque than the control bar for emphasis.
                        Color.Black.copy(alpha = 0.5f), // Or Color.Black.copy(alpha = 0.4f)
                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp) // Soft rounded corners for the background
                    )
                    .padding(top = innerPadding.calculateTopPadding())
                    // Padding inside the text's background, around the text itself
                    .padding(16.dp)
                    .align(Alignment.TopCenter) // Aligns the Text's box to the top of the parent Box
            )

            // Instructional Text and Controls Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Controls Row (your existing controls)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth() // The row itself spans full width for SpaceBetween
                            // Background for the control bar (pill shape)
                            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                            // Padding inside the control bar, around the buttons
                            .padding(vertical = 16.dp, horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Switch Camera Button
                        IconButton(
                            onClick = { cameraPreviewViewModel.flipCamera() }, // Ensure flipCamera is in your ViewModel
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Cameraswitch,
                                contentDescription = "Switch Camera",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        // Take Photo Button
                        Button(
                            onClick = {
                                cameraPreviewViewModel.captureImage(
                                    context = context,
                                    onImageCapturedSuccess = visualLensViewModel::detectObjectInImage
                                )
                            },
                            modifier = Modifier.size(72.dp),
                            cornerRadius = 36.dp, // This makes the button circular
                            colors = ButtonColors(
                                color = MiuixTheme.colorScheme.primary,
                                disabledColor = MiuixTheme.colorScheme.primary.copy(alpha = 0.5f),
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PhotoCamera,
                                contentDescription = "Take Photo",
                                modifier = Modifier.size(36.dp),
                                tint = Color.White // Ensure MiuixTheme.colorScheme.primary contrasts well with White
                                // Or consider using MiuixTheme.colorScheme.onPrimary
                            )
                        }

                        // History Button
                        IconButton(
                            onClick = { isHistoryBottomSheetVisible = true },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.History,
                                contentDescription = "View History",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }

            // Your Four Corner Overlay Icon (remains centered on the screen)
            Icon(
                painter = painterResource(id = R.drawable.four_corner),
                contentDescription = null, // Decorative, so contentDescription can be null
                tint = Color.White,
                modifier = Modifier
                    .fillMaxSize() // Icon will attempt to fill, actual size depends on Painter and Icon impl.
                    .align(Alignment.Center) // Aligns the Icon's box to the center of the parent Box
                    .padding(horizontal = 16.dp), // Padding applied to the space the icon can occupy
            )
        }

        val showDetectionDialog = state.imageFile != null &&
                state.detectedObjects != null &&
                state.originalImageWidth != null &&
                state.originalImageHeight != null

        if (showDetectionDialog) {
            ObjectDetectionResultsDialog(
                imageFile = state.imageFile!!,
                detectionResult = state.detectedObjects!!,
                originalImageWidth = state.originalImageWidth!!,
                originalImageHeight = state.originalImageHeight!!,
                isLoading = state.isDialogLoading, // Pass loading state specific to detection
                onDismissRequest = { visualLensViewModel.dismissImageDialog() },
                onObjectClick = {
                    isHistoryBottomSheetVisible = false
                    visualLensViewModel.getObjectDetails(it)
                }
            )
        }

        if (isHistoryBottomSheetVisible) {
            HistoryBottomSheet(
                historyItems = state.histories,
                onDismissRequest = {
                    isHistoryBottomSheetVisible = false
                },
                onClick = visualLensViewModel::onHistoryClick,
                onDeleteClick = visualLensViewModel::onHistoryDelete
            )
        }

        if (state.isLoading) {
            FullScreenLoading()
        }
    }
}