package com.bisbiai.app.ui.screen.flashcard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bisbiai.app.ui.components.BaseTopAppBar
import com.bisbiai.app.ui.components.FullScreenLoading
import com.bisbiai.app.ui.screen.flashcard.component.CardItem
import com.bisbiai.app.ui.screen.flashcard.component.ProgressIndicator
import kotlinx.coroutines.flow.collectLatest
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun FlashcardScreen(
    modifier: Modifier = Modifier, // This modifier is passed from the caller
    viewModel: FlashcardViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val pagerState = rememberPagerState(
        initialPage = state.currentFlashcardIndex,
        initialPageOffsetFraction = 0f,
        pageCount = {
            state.flashcards.size
        }
    )

    LaunchedEffect(key1 = Unit) {
        viewModel.errorMessage.collectLatest { message ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message = message)
        }
    }

    Scaffold(
        topBar = { BaseTopAppBar(onNavigateUp = onNavigateUp, title = "BISBI Boost") },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        if (state.flashcards.isNotEmpty()) {
            Box(
                modifier = Modifier // Use a Box for layering
                    .fillMaxSize()
                    .padding(innerPadding) // Apply innerPadding to the Box
            ) {
                // Layer 1: HorizontalPager (in the background)
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(), // Pager fills the entire Box
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 32.dp), // Pager's own content padding
                    pageSpacing = 16.dp
                ) { index ->
                    CardItem(
                        // Modifier.padding(innerPadding) is no longer needed here as the parent Box handles it
                        flashcard = state.flashcards[index]
                    )
                }

                // Layer 2: Controls (Column on top of the Pager)
                Column(
                    // Apply the original modifier passed to FlashcardScreen here,
                    // but ensure it doesn't conflict with fillMaxSize or add redundant scaffold padding.
                    // The .padding(innerPadding) was removed from here as it's on the parent Box.
                    modifier = modifier // Original modifier from FlashcardScreen function signature
                        .fillMaxSize() // Make this Column fill the Box to position children at top/bottom
                        .padding(top = 8.dp) // Your original top padding for controls
                        .padding(horizontal = 32.dp), // Your original horizontal padding for controls
                    horizontalAlignment = Alignment.End, // As per your original Column for controls
                    verticalArrangement = Arrangement.spacedBy(8.dp) // As per your original Column
                ) {
                    ProgressIndicator(
                        progress = {
                            if (state.flashcards.isNotEmpty()) {
                                (pagerState.currentPage + 1).toFloat() / state.flashcards.size.toFloat()
                            } else {
                                0f
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        current = "${pagerState.currentPage + 1}/${state.flashcards.size}",
                    )

                    Spacer(modifier = Modifier.weight(1f)) // Pushes the Listen Row to the bottom

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center, // Centers Icon and Text within the Row
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally) // Centers the Row itself horizontally
                            .padding(vertical = 16.dp)
                            .clickable {
                                viewModel.playAudio(
                                    text = state.flashcards[pagerState.currentPage].objectName,
                                    context = context
                                )
                            }
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.playAudio(
                                    text = state.flashcards[pagerState.currentPage].objectName,
                                    context = context
                                )
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .offset(x = (-4).dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Listen to example: ${state.flashcards[pagerState.currentPage].objectName}",
                                tint = MiuixTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Listen",
                            color = MiuixTheme.colorScheme.primary,
                            style = MiuixTheme.textStyles.headline1.copy(color = MiuixTheme.colorScheme.primary),
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding), // Apply innerPadding here too for the empty state
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CardMembership,
                        contentDescription = "Flashcard Icon",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(bottom = 16.dp),
                        tint = MiuixTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "You have to scan objects or create a scenario first to use flashcards!",
                        style = MiuixTheme.textStyles.title3,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 16.dp)
                    )
                }
            }
        }

        if (isLoading) {
            FullScreenLoading()
        }
    }
}