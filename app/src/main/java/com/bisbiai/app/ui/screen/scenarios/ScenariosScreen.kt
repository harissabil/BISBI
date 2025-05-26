package com.bisbiai.app.ui.screen.scenarios

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bisbiai.app.data.remote.dto.GenerateLessonResponse
import com.bisbiai.app.ui.UserProgressViewModel
import com.bisbiai.app.ui.components.FullScreenLoading
import com.bisbiai.app.ui.screen.scenario_detail.components.ScenarioTitleItem
import com.bisbiai.app.ui.theme.BISBIAITheme
import com.bisbiai.app.ui.theme.spacing
import kotlinx.coroutines.flow.collectLatest
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenariosScreen(
    modifier: Modifier = Modifier,
    userProgressViewModel: UserProgressViewModel,
    viewModel: ScenariosViewModel = hiltViewModel(),
    onGoToLessonDetail: (GenerateLessonResponse) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val proficiencyLevels = remember { listOf("Beginner", "Intermediate", "Advanced") }
    var selectedProficiencyLevel by remember { mutableStateOf(proficiencyLevels[1]) } // Default ke Intermediate
    var isProficiencyDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.errorMessage.collectLatest { message ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message = message)
        }
    }


    LaunchedEffect(key1 = state.isGoingToDetail) {
        if (state.isGoingToDetail && state.lessonData != null) {
            state.lessonData?.let { detail ->
                onGoToLessonDetail(detail)
                viewModel.resetState() // Reset state after navigating
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(24.dp)
        ) {
            item {
                Text(
                    text = "Scenario Challenge",
                    style = MiuixTheme.textStyles.title2,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            item {
                Card(
                    cornerRadius = 16.dp,
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        // Situation Description
                        Text(
                            text = "Describe a situation or conversation:",
                            style = MiuixTheme.textStyles.headline2,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        TextField(
                            value = state.situationDescription,
                            onValueChange = viewModel::onSituationDescriptionChanged,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp), // Memberi tinggi minimal untuk beberapa baris
                            minLines = 3,
                            label = "e.g., Ordering food at a restaurant, asking for directions, job interview...",
                            useLabelAsPlaceholder = true
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Proficiency Level
                        Text(
                            text = "Proficiency Level:",
                            style = MiuixTheme.textStyles.headline2,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        ExposedDropdownMenuBox(
                            expanded = isProficiencyDropdownExpanded,
                            onExpandedChange = {
                                isProficiencyDropdownExpanded = !isProficiencyDropdownExpanded
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextField(
                                value = selectedProficiencyLevel,
                                onValueChange = {}, // Tidak perlu karena readOnly
                                readOnly = true,
                                trailingIcon = {
                                    Row {
                                        val icon =
                                            if (isProficiencyDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = "",
                                            tint = MiuixTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
                                    }
                                },
                                modifier = Modifier
                                    .menuAnchor() // Penting untuk menghubungkan TextField dengan Menu
                                    .fillMaxWidth(),
                                cornerRadius = 8.dp,
                            )
                            ExposedDropdownMenu(
                                expanded = isProficiencyDropdownExpanded,
                                onDismissRequest = { isProficiencyDropdownExpanded = false },
                                containerColor = MiuixTheme.colorScheme.surface,
                            ) {
                                proficiencyLevels.forEach { level ->
                                    DropdownMenuItem(
                                        text = { Text(level) },
                                        colors = MenuItemColors(
                                            textColor = MiuixTheme.colorScheme.onSurface,
                                            leadingIconColor = MiuixTheme.colorScheme.onSurface,
                                            trailingIconColor = MiuixTheme.colorScheme.onSurface,
                                            disabledTextColor = MiuixTheme.colorScheme.onSurface,
                                            disabledLeadingIconColor = MiuixTheme.colorScheme.onSurface,
                                            disabledTrailingIconColor = MiuixTheme.colorScheme.onSurface
                                        ),
                                        onClick = {
                                            selectedProficiencyLevel = level
                                            isProficiencyDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Generate Button
                        Button(
                            onClick = {
                                viewModel.getScenarioLesson(
                                    userProficiencyLevel = selectedProficiencyLevel,
                                )
                            },
                            enabled = state.situationDescription.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            cornerRadius = 12.dp,
                            colors = ButtonDefaults.buttonColors(
                                color = MiuixTheme.colorScheme.primary,
                                disabledColor = MiuixTheme.colorScheme.disabledSecondaryVariant
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send, // Atau ikon pesawat kertas
                                contentDescription = "Generate Lesson Icon",
                                tint = if (state.situationDescription.isNotBlank()) {
                                    Color.White
                                } else {
                                    MiuixTheme.colorScheme.onSurface
                                },
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Generate My Lesson!",
                                color = if (state.situationDescription.isNotBlank()) {
                                    Color.White
                                } else {
                                    MiuixTheme.colorScheme.onSurface
                                },
                            )
                        }
                    }
                }
            }

            // History Section
            if (state.scenarioHistory.isNotEmpty()) {
                item {
                    Text(
                        text = "Your Scenario History",
                        style = MiuixTheme.textStyles.headline2,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            }

            items(items = state.scenarioHistory, key = { it.id }) { scenario ->
                ScenarioTitleItem(
                    title = scenario.lessonData.scenarioTitle,
                    onClick = {
                        viewModel.onScenarioClicked(scenario.id)
                    }
                )
                // if not last item, add a spacer
                if (scenario != state.scenarioHistory.last()) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (state.isLoading) {
            FullScreenLoading()
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScenariosScreenPreviewEmpty() {
    BISBIAITheme { // Ganti dengan nama tema Anda
        ScenariosScreen(
            onGoToLessonDetail = {},
            userProgressViewModel = hiltViewModel()
        )
    }
}