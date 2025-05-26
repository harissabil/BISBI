package com.bisbiai.app.ui.screen.voice_gym.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bisbiai.app.data.remote.dto.PronunciationAssessmentResponse
import com.bisbiai.app.data.remote.dto.WordsItem
import com.bisbiai.app.ui.theme.BISBIAITheme
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.roundToInt

// Warna Skor
val scoreRed = Color(0xFFE53935) // Merah untuk skor rendah (< 50%)
val scoreOrange = Color(0xFFF57C00) // Oranye untuk skor sedang (50-79%)
val scoreGreen = Color(0xFF10b981) // Hijau untuk skor bagus (>= 80%)
val mispronunciationBackground = Color(0xFFFFEBEE) // Background merah muda sangat terang
val mispronunciationText = scoreRed
val mispronunciationChipBackground = Color(0xFFFFF3E0) // Background oranye muda untuk chip mispronunciation
val mispronunciationChipText = scoreOrange

// Fungsi helper untuk mendapatkan warna berdasarkan skor (0-100)
fun getScoreColor(score: Int): Color {
    return when {
        score < 50 -> scoreRed
        score < 80 -> scoreOrange
        else -> scoreGreen
    }
}

// Fungsi helper untuk mengonversi 'Any' ke Int (0-100), dengan default 0 jika tidak valid
fun Any?.toIntScore(): Int {
    return when (this) {
        is Number -> this.toDouble().coerceIn(0.0, 100.0).roundToInt()
        is String -> this.toDoubleOrNull()?.coerceIn(0.0, 100.0)?.roundToInt() ?: 0
        else -> 0
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PronunciationScoreDialog(
    assessmentData: PronunciationAssessmentResponse?,
    onDismissRequest: () -> Unit
) {
    if (assessmentData == null) {
        // Bisa tampilkan dialog loading atau tidak sama sekali jika data belum siap
        return
    }

    val pronunciationScore = assessmentData.pronunciationScore.toIntScore()
    val accuracyScore = assessmentData.accuracyScore.toIntScore()
    val fluencyScore = assessmentData.fluencyScore.toIntScore()
    val completenessScore = assessmentData.completenessScore.toIntScore()
    val prosodyScore = assessmentData.prosodyScore.toIntScore()

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier
            .wrapContentHeight()
            .padding(vertical = 24.dp), // Padding dialog
    ) {
        Surface( // Surface untuk menerapkan shape, color, shadow, dll.
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp), // Sudut dialog yang lebih bulat
            color = MiuixTheme.colorScheme.surface, // Warna background dialog
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp) // Padding internal dialog
            ) {
                // --- Bagian Skor Utama ---
                StarRating(score = pronunciationScore)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pronunciation Score",
                    style = MiuixTheme.textStyles.title3.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MiuixTheme.colorScheme.onSurface
                )
                Text(
                    text = "$pronunciationScore%",
                    style = MiuixTheme.textStyles.title3.copy(
                        fontSize = 48.sp, // Ukuran font besar untuk skor utama
                        fontWeight = FontWeight.ExtraBold,
                        color = getScoreColor(pronunciationScore)
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- Grid Skor Detail ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ScoreDetailItem("Accuracy", accuracyScore, Modifier.weight(1f))
                    ScoreDetailItem("Fluency", fluencyScore, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ScoreDetailItem("Completeness", completenessScore, Modifier.weight(1f))
                    ScoreDetailItem("Prosody", prosodyScore, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- Bagian Word Breakdown ---
                Text(
                    text = "Word Breakdown",
                    style = MiuixTheme.textStyles.headline2.copy(fontWeight = FontWeight.Bold),
                    color = MiuixTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (assessmentData.words.isEmpty()) {
                    Text(
                        "No word breakdown available.",
                        style = MiuixTheme.textStyles.body1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp)
                    )
                } else {
                    assessmentData.words.forEach { wordItem ->
                        WordBreakdownItem(wordItem = wordItem)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                // Anda bisa menambahkan tombol "OK" atau "Close" di sini jika diperlukan
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismissRequest,
                        text = "Close" // "Close"
                    )
                    // Jika Anda menggunakan Miuix TextButton, pastikan propertinya sesuai
                    // contoh: top.yukonga.miuix.kmp.widget.TextButton(onClick = onDismissRequest) { Text("Tutup") }
                }
            }
        }
    }
}

@Composable
fun StarRating(score: Int, maxStars: Int = 5) {
    val filledStars = remember(score) {
        // Konversi skor 0-100 ke 0-5 bintang
        (score / (100f / maxStars)).coerceIn(0f, maxStars.toFloat()).roundToInt()
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            Icon(
                imageVector = if (i <= filledStars) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = if (i <= filledStars) "Filled Star" else "Empty Star",
                tint = if (i <= filledStars) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.outline,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun ScoreDetailItem(label: String, score: Int, modifier: Modifier = Modifier) {
    Card( // Menggunakan Card dari Miuix
        modifier = modifier,
        cornerRadius = 12.dp,
        color = MiuixTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp), // Padding disesuaikan
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
            Text(
                text = "$score%",
                style = MiuixTheme.textStyles.title2.copy(fontWeight = FontWeight.Bold),
                color = getScoreColor(score),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun WordBreakdownItem(wordItem: WordsItem) {
    val wordScore = wordItem.accuracyScore.toIntScore()
    val isMispronounced = wordItem.errorType.equals("Mispronunciation", ignoreCase = true) ||
            wordItem.errorType.equals("Omission", ignoreCase = true) || // Omission juga bisa dianggap salah ucap
            wordItem.errorType.equals("Insertion", ignoreCase = true)   // Insertion juga

    Card(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 10.dp,
        color = if (isMispronounced) mispronunciationBackground else Color(0xFFd5f7ec) // Warna background berbeda jika salah
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(
                    text = wordItem.word,
                    style = MiuixTheme.textStyles.body2.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                    color = Color.Black,
                    maxLines = 1 // Batasi kata jika terlalu panjang untuk satu baris di item
                )
                if (isMispronounced && wordItem.errorType.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    MispronunciationChip(errorType = wordItem.errorType)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "$wordScore%",
                style = MiuixTheme.textStyles.body2.copy(fontWeight = FontWeight.Bold),
                color = getScoreColor(wordScore)
            )
        }
    }
}

@Composable
fun MispronunciationChip(errorType: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(mispronunciationChipBackground)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.WarningAmber,
            contentDescription = "Warning",
            tint = mispronunciationChipText,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = errorType.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }, // Kapitalisasi huruf pertama
            color = mispronunciationChipText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


// --- Preview ---
val sampleWords = listOf(
    WordsItem(accuracyScore = 0, errorType = "Mispronunciation", word = "Xenoblade", phonemes = emptyList()),
    WordsItem(accuracyScore = 0, errorType = "Mispronunciation", word = "Chronicles", phonemes = emptyList()),
    WordsItem(accuracyScore = 75, errorType = "", word = "is", phonemes = emptyList()),
    WordsItem(accuracyScore = 90, errorType = "", word = "fun", phonemes = emptyList())
)
val sampleAssessmentData = PronunciationAssessmentResponse(
    accuracyScore = 60, // contoh sedang
    completenessScore = 85, // contoh bagus
    prosodyScore = 40, // contoh rendah
    words = sampleWords,
    fluencyScore = 70, // contoh sedang
    pronunciationScore = 65, // contoh sedang
    recognizedText = "Xenoblade Chronicles is fun"
)

@Preview(showBackground = true, name = "Pronunciation Score Dialog Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Pronunciation Score Dialog Dark")
@Composable
fun PronunciationScoreDialogPreview() {
    BISBIAITheme {
        // Untuk preview dialog, kita bungkus dalam Box agar terlihat seperti dialog
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)), // Latar belakang redup
            contentAlignment = Alignment.Center
        ) {
            PronunciationScoreDialog(
                assessmentData = sampleAssessmentData,
                onDismissRequest = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Score Detail Item Preview")
@Composable
fun ScoreDetailItemPreview() {
    BISBIAITheme {
        Row {
            ScoreDetailItem(label = "Accuracy", score = 88, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            ScoreDetailItem(label = "Fluency", score = 45, modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true, name = "Word Breakdown Item Preview")
@Composable
fun WordBreakdownItemPreview() {
    BISBIAITheme {
        Column {
            WordBreakdownItem(wordItem = sampleWords[0]) // Mispronunciation
            Spacer(Modifier.height(8.dp))
            WordBreakdownItem(wordItem = sampleWords[2]) // Good
        }
    }
}