package com.bisbiai.app.ui.screen.scenario_detail

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bisbiai.app.data.remote.dto.Example
import com.bisbiai.app.data.remote.dto.GenerateLessonResponse
import com.bisbiai.app.data.remote.dto.GrammarTipsItem
import com.bisbiai.app.data.remote.dto.KeyPhrasesItem
import com.bisbiai.app.data.remote.dto.Phrase
import com.bisbiai.app.data.remote.dto.ScenarioTitle
import com.bisbiai.app.data.remote.dto.Term
import com.bisbiai.app.data.remote.dto.Tip
import com.bisbiai.app.data.remote.dto.VocabularyItem
import com.bisbiai.app.ui.UserProgressViewModel
import com.bisbiai.app.ui.components.BaseTopAppBar
import com.bisbiai.app.ui.components.FullScreenLoading
import com.bisbiai.app.ui.screen.scenario_detail.components.GrammarTipItem
import com.bisbiai.app.ui.screen.scenario_detail.components.KeyPhraseListItem
import com.bisbiai.app.ui.screen.scenario_detail.components.ScenarioTitleItem
import com.bisbiai.app.ui.screen.scenario_detail.components.SectionTitle
import com.bisbiai.app.ui.screen.scenario_detail.components.VocabularyListItem
import com.bisbiai.app.ui.theme.BISBIAITheme
import kotlinx.coroutines.flow.collectLatest
import top.yukonga.miuix.kmp.basic.Scaffold

@Composable
fun ScenarioDetailScreen(
    modifier: Modifier = Modifier,
    lessonData: GenerateLessonResponse,
    userProgressViewModel: UserProgressViewModel,
    viewModel: ScenarioDetailViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        userProgressViewModel.onScenarioMastered()
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.errorMessage.collectLatest { message ->
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message = message)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BaseTopAppBar(
                title = "Scene Lesson",
                onNavigateUp = onNavigateUp
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = innerPadding.calculateTopPadding())
                .padding(16.dp)
        ) {
            // Scenario Title Section
            ScenarioTitleItem(title = lessonData.scenarioTitle)

            Spacer(modifier = Modifier.height(24.dp))

            // Key Vocabulary Section
            SectionTitle("Key Vocabulary")
            lessonData.vocabulary.forEach { vocabItem ->
                VocabularyListItem(
                    term = vocabItem.term,
                    onListenClick = {
                        viewModel.playAudio(vocabItem.term.en, context)
                    }, // Kirim teks English
                    onXpChipClick = {
                        userProgressViewModel.onWordsLearnedFromScenario()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Key Phrases Section
            SectionTitle("Key Phrases")
            lessonData.keyPhrases.forEach { phraseItem ->
                KeyPhraseListItem(
                    phrase = phraseItem.phrase,
                    onListenClick = {
                        viewModel.playAudio(phraseItem.phrase.en, context)
                    },
                    onXpChipClick = {
                        userProgressViewModel.onWordsLearnedFromScenario()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Grammar Tips Section
            SectionTitle("Grammar Tips")
            lessonData.grammarTips.forEach { tipItem ->
                GrammarTipItem(
                    tip = tipItem.tip,
                    example = tipItem.example,
                    onListenClick = {
                        viewModel.playAudio(tipItem.example.en, context)
                    },
                    onXpChipClick = {
                        userProgressViewModel.onWordsLearnedFromScenario()
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp)) // Padding akhir
        }

        if (isLoading) {
            FullScreenLoading()
        }
    }
}

// --- Preview ---
val sampleScenarioTitle = ScenarioTitle(
    en = "Talking with Your Mentor about Merging Code",
    id = "Berbicara dengan Mentor tentang Menggabungkan Kode"
)
val sampleVocabulary = listOf(
    VocabularyItem(Term(en = "mentor", id = "mentor")),
    VocabularyItem(Term(en = "merge", id = "menggabungkan")),
    VocabularyItem(Term(en = "library", id = "perpustakaan (kode)")),
    VocabularyItem(Term(en = "code", id = "kode")),
    VocabularyItem(Term(en = "project", id = "proyek")),
    VocabularyItem(Term(en = "decision", id = "keputusan"))
)
val sampleKeyPhrases = listOf(
    KeyPhrasesItem(
        Phrase(
            en = "Who should merge the library?",
            id = "Siapa yang harus menggabungkan perpustakaan (kode)?"
        )
    ),
    KeyPhrasesItem(
        Phrase(
            en = "Should I do it or you?",
            id = "Haruskah saya yang melakukannya atau Anda?"
        )
    ),
    KeyPhrasesItem(
        Phrase(
            en = "Can you help me merge the code?",
            id = "Bisakah Anda membantu saya menggabungkan kode?"
        )
    ),
    KeyPhrasesItem(
        Phrase(
            en = "I am not sure how to merge.",
            id = "Saya tidak yakin bagaimana cara menggabungkan."
        )
    )
)
val sampleGrammarTips = listOf(
    GrammarTipsItem(
        tip = Tip(
            en = "Use 'should' to ask about what is the best thing to do.",
            id = "Gunakan 'should' untuk bertanya tentang apa yang sebaiknya dilakukan."
        ),
        example = Example(
            en = "Should I merge the library?",
            id = "Haruskah saya menggabungkan perpustakaan (kode)?"
        )
    )
)
val sampleLessonData = GenerateLessonResponse(
    vocabulary = sampleVocabulary,
    keyPhrases = sampleKeyPhrases,
    scenarioTitle = sampleScenarioTitle,
    grammarTips = sampleGrammarTips
)

@Preview(showBackground = true, name = "Scenario Detail Screen Light")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Scenario Detail Screen Dark"
)
@Composable
fun ScenarioDetailScreenPreview() {
    BISBIAITheme {
        ScenarioDetailScreen(
            lessonData = sampleLessonData,
            onNavigateUp = {},
            userProgressViewModel = hiltViewModel(),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VocabularyListItemPreview() {
    BISBIAITheme {
        VocabularyListItem(term = sampleVocabulary.first().term, onListenClick = {}, onXpChipClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun KeyPhraseListItemPreview() {
    BISBIAITheme {
        KeyPhraseListItem(phrase = sampleKeyPhrases.first().phrase, onListenClick = {}, onXpChipClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun GrammarTipItemPreview() {
    BISBIAITheme {
        GrammarTipItem(
            tip = sampleGrammarTips.first().tip,
            example = sampleGrammarTips.first().example,
            onListenClick = {},
            onXpChipClick = {}
        )
    }
}
