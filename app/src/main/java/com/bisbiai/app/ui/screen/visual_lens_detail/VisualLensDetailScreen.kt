package com.bisbiai.app.ui.screen.visual_lens_detail

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bisbiai.app.data.remote.dto.Description
import com.bisbiai.app.data.remote.dto.ExampleSentencesItem
import com.bisbiai.app.data.remote.dto.GetObjectDetailsResponse
import com.bisbiai.app.data.remote.dto.ObjectName
import com.bisbiai.app.data.remote.dto.RelatedAdjectivesItem
import com.bisbiai.app.ui.components.BaseTopAppBar
import com.bisbiai.app.ui.components.FullScreenLoading
import com.bisbiai.app.ui.screen.visual_lens_detail.components.AdjectiveChip
import com.bisbiai.app.ui.screen.visual_lens_detail.components.ExampleSentenceCard
import com.bisbiai.app.ui.theme.BISBIAITheme
import kotlinx.coroutines.flow.collectLatest
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun VisualLensDetailScreen(
    modifier: Modifier = Modifier,
    objectDetails: GetObjectDetailsResponse,
    onNavigateUp: () -> Unit,
    viewModel: VisualLensDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.errorMessage.collectLatest { message ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message = message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BaseTopAppBar(
                title = "Object Details",
                onNavigateUp = onNavigateUp
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = innerPadding.calculateTopPadding())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // --- Title Section ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = objectDetails.objectName.en,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MiuixTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    // painter = painterResource(id = R.drawable.ic_listen), // Use your custom icon
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp, // Or use a Material Icon
                    contentDescription = "Listen to ${objectDetails.objectName.en}",
                    tint = MiuixTheme.colorScheme.primary, // Or MaterialTheme.colorScheme.primary
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { viewModel.playAudio(objectDetails.objectName.en, context) }
                )
            }
            Text(
                text = objectDetails.objectName.id,
                fontSize = 18.sp,
                color = MiuixTheme.colorScheme.onSurfaceSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Description Section ---
            Text(
                text = "Description:",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MiuixTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CardDefaults.DefaultColor(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = objectDetails.description.en,
                            fontSize = 16.sp,
                            color = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            // painter = painterResource(id = R.drawable.ic_listen),
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen to description",
                            tint = MiuixTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    viewModel.playAudio(
                                        objectDetails.description.en,
                                        context
                                    )
                                }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = objectDetails.description.id,
                        fontSize = 16.sp,
                        color = MiuixTheme.colorScheme.onSurfaceSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Example Sentences Section ---
            Text(
                text = "Example Sentences:",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MiuixTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            objectDetails.exampleSentences.forEach { sentenceItem ->
                ExampleSentenceCard(
                    sentenceItem = sentenceItem,
                    onPlayAudio = { viewModel.playAudio(it, context) })
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Related Adjectives Section ---
            Text(
                text = "Related Adjectives:",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MiuixTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(objectDetails.relatedAdjectives) { adjectiveItem ->
                    AdjectiveChip(
                        adjectiveItem = adjectiveItem,
                        onPlayAudio = {
                            viewModel.playAudio(adjectiveItem.en, context)
                        }
                    )
                }
            }
        }

        if (isLoading) {
            FullScreenLoading()
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun VisualLensDetailScreenPreview() {
    // Sample data for preview
    val sampleDetails = GetObjectDetailsResponse(
        objectName = ObjectName(en = "Jacket and Cap", id = "Jaket dan Topi"),
        description = Description(
            en = "A dark blue jacket and a cap are hanging on a rod. The jacket appears to be a uniform or work jacket, and the cap is placed on top of the rod.",
            id = "Sebuah jaket biru tua dan topi tergantung pada sebuah batang. Jaket tersebut tampaknya merupakan jaket seragam atau jaket kerja, dan topi diletakkan di atas batang."
        ),
        exampleSentences = listOf(
            ExampleSentencesItem(
                en = "He hung his jacket and cap on the rod after coming home.",
                id = "Dia menggantung jaket dan topinya di batang setelah pulang ke rumah."
            ),
            ExampleSentencesItem(
                en = "The blue jacket looks like it belongs to a worker or an employee.",
                id = "Jaket biru itu tampak seperti milik seorang pekerja atau karyawan."
            )
        ),
        relatedAdjectives = listOf(
            RelatedAdjectivesItem(
                en = "dark",
                id = "gelap"
            ), // Assuming 'id' might be Indonesian translation
            RelatedAdjectivesItem(en = "uniform", id = "seragam")
        )
    )

    // Using MaterialTheme for basic theming. Replace with your app's theme if you have one.
    BISBIAITheme {
        VisualLensDetailScreen(objectDetails = sampleDetails, onNavigateUp = {})
    }
}