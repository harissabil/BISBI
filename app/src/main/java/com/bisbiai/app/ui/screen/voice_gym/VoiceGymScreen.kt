package com.bisbiai.app.ui.screen.voice_gym // Sesuaikan dengan package Anda

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.bisbiai.app.ui.UserProgressViewModel
import com.bisbiai.app.ui.components.FullScreenLoading
import com.bisbiai.app.ui.screen.voice_gym.components.PronunciationScoreDialog
import com.bisbiai.app.ui.screen.voice_gym.components.PulsatingRecordingButton
import com.bisbiai.app.ui.screen.voice_gym.components.toIntScore
import com.bisbiai.app.ui.theme.BISBIAITheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.util.concurrent.TimeUnit

@Composable
fun VoiceGymScreen(
    modifier: Modifier = Modifier,
    userProgressViewModel: UserProgressViewModel,
    viewModel: VoiceGymViewModel = hiltViewModel(), // Uncomment jika menggunakan ViewModel
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState() // Jika pakai ViewModel
    var recordingTimeMillis by remember { mutableLongStateOf(0L) }

    // Efek untuk timer perekaman
    LaunchedEffect(state.isRecording) {
        if (state.isRecording) {
            recordingTimeMillis = 0L // Reset timer saat mulai
            while (state.isRecording) {
                delay(1000) // Update setiap detik
                if (state.isRecording) { // Cek lagi karena state bisa berubah saat delay
                    recordingTimeMillis += 1000
                }
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val isPhraseEntered = state.phraseToPractice.isNotBlank()

    val examplePhrases = remember {
        listOf(
            "What time does the museum open tomorrow morning?",
            "Can you recommend a good local restaurant?",
            "How do I get to the nearest train station?",
            "I would like to book a table for two."
        )
    }
    var currentExampleIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = Unit) {
        viewModel.errorMessage.collectLatest { message ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message = message)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startRecording(context)
        } else {
            // Show error about permission denial
            viewModel.showError("Microphone permission is required for recording.")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = innerPadding.calculateTopPadding())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Voice Gym",
                style = MiuixTheme.textStyles.title2,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Card untuk Input Frasa
            Card(
                cornerRadius = 16.dp,
                modifier = Modifier.fillMaxWidth().border(1.dp, MiuixTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Enter or use an example phrase to practice:",
                        style = MiuixTheme.textStyles.subtitle,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    TextField(
                        value = state.phraseToPractice,
                        onValueChange = viewModel::setReferenceText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp),
                        minLines = 2,
                        label = "Type or paste a phrase here...",
                        useLabelAsPlaceholder = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Tombol "Use Example"
                        OutlinedButton(
                            onClick = {
                                viewModel.setReferenceText(examplePhrases[currentExampleIndex])
                                currentExampleIndex =
                                    (currentExampleIndex + 1) % examplePhrases.size
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(
                                width = 1.dp,
                                color = Color(0xFFF97316) // Warna outline button
                            ),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                contentColor = Color(0xFFF97316), // Warna teks untuk outline button
                                containerColor = Color.Transparent,
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.StarOutline,
                                contentDescription = "Use Example",
                                tint = Color(0xFFF97316) // Warna ikon untuk outline button
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Use Example",
                                color = Color(0xFFF97316) // Warna teks untuk outline button
                            )
                        }

                        // Tombol "Listen" (muncul jika ada frasa)
                        if (isPhraseEntered) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.playAudio(
                                        state.phraseToPractice,
                                        context
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MiuixTheme.colorScheme.primary // Warna outline button
                                ),
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    contentColor = MiuixTheme.colorScheme.primary, // Warna teks untuk outline button
                                    containerColor = Color.Transparent,
                                ),
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Listen",
                                    tint = MiuixTheme.colorScheme.primary // Ikon putih di atas background primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Listen",
                                    color = MiuixTheme.colorScheme.primary // Teks putih
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Card untuk Tombol Rekam
            Card(
                cornerRadius = 16.dp,
                modifier = Modifier.fillMaxWidth().border(1.dp, MiuixTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 32.dp,
                            horizontal = 16.dp
                        ), // Padding lebih besar vertikal
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!state.isRecording) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isPhraseEntered) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.disabledPrimary
                                )
                                .clickable(
                                    enabled = isPhraseEntered,
                                    onClick = {
                                        // Check permission before recording
                                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                                            == PackageManager.PERMISSION_GRANTED) {
                                            viewModel.startRecording(context)
                                        } else {
                                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    },
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = LocalIndication.current
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = "Record",
                                tint = if (isPhraseEntered) Color.White else MiuixTheme.colorScheme.disabledOnPrimary,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isPhraseEntered) "Tap to Record" else "Enter a phrase first",
                            style = MiuixTheme.textStyles.headline2,
                            textAlign = TextAlign.Center,
                            color = MiuixTheme.colorScheme.onSurface
                        )
                    } else {
                        // Tampilan SAAT Merekam
                        PulsatingRecordingButton(
                            isRecording = true,
                            baseColor = Color(0xFFe74848), // Warna dasar saat merekam (merah)
                            pulseColor = Color(0xFFe74848), // Warna saat pulsasi (merah muda)
                            onClick = {
                                viewModel.stopRecording()
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Recording...",
                            style = MiuixTheme.textStyles.title3,
                            color = Color(0xFFe74848), // Teks "Recording..." berwarna merah
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatMillisToMmSs(recordingTimeMillis),
                            style = MiuixTheme.textStyles.headline2,
                            color = MiuixTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                viewModel.stopRecording()
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f) // Tombol tidak full width
                                .height(50.dp),
                            cornerRadius = 12.dp,
                            colors = ButtonDefaults.buttonColors(
                                color = MiuixTheme.colorScheme.secondary, // Warna tombol stop
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = "Stop Recording",
                                tint = MiuixTheme.colorScheme.onSecondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Stop Recording",
                                color = MiuixTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
            }
        }

        if (state.pronunciationAssessmentResponse != null) {
            PronunciationScoreDialog(
                assessmentData = state.pronunciationAssessmentResponse,
                onDismissRequest = {
                    userProgressViewModel.onPronunciationScored(state.pronunciationAssessmentResponse!!.pronunciationScore.toIntScore())
                    viewModel.dismissDialog()
                }
            )
        }

        if (state.isLoading) {
            FullScreenLoading()
        }
    }
}

fun formatMillisToMmSs(millis: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format("%02d:%02d", minutes, seconds)
}


@Preview(showBackground = true, name = "Voice Gym Empty")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Voice Gym Empty Dark"
)
@Composable
fun VoiceGymScreenPreviewEmpty() {
    BISBIAITheme {
        VoiceGymScreen(
            userProgressViewModel = hiltViewModel() // Ganti dengan ViewModel yang sesuai
        )
    }
}