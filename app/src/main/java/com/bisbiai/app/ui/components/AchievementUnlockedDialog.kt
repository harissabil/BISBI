package com.bisbiai.app.ui.components

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bisbiai.app.data.local.entity.AchievementEntity
import com.bisbiai.app.ui.theme.BISBIAITheme
import top.yukonga.miuix.kmp.theme.MiuixTheme

// Warna yang mungkin berguna, bisa disesuaikan atau diambil dari MiuixTheme
val goldColor = Color(0xFFFFD700)
val silverColor = Color(0xFFC0C0C0)
val bronzeColor = Color(0xFFCD7F32)

@Composable
fun AchievementUnlockedDialog(
    achievement: AchievementEntity,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card( // Menggunakan Card dari Material 3 untuk dialog ini, atau Miuix Card jika preferensi
            shape = RoundedCornerShape(20.dp),
            modifier = modifier
                .fillMaxWidth(0.9f) // Dialog tidak full width
                .wrapContentHeight(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MiuixTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Emoji sebagai Ikon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            // Beri background berdasarkan XP reward atau tipe achievement (opsional)
                            // Contoh sederhana:
                            if (achievement.xpReward >= 25) goldColor.copy(alpha = 0.2f)
                            else if (achievement.xpReward >= 15) silverColor.copy(alpha = 0.2f)
                            else MiuixTheme.colorScheme.primaryContainer
                        )
                ) {
                    Text(
                        text = achievement.icon, // Ini adalah emoji string
                        fontSize = 48.sp // Ukuran besar untuk emoji
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Achievement Unlocked!",
                    style = MiuixTheme.textStyles.title1.copy(fontWeight = FontWeight.Bold),
                    color = MiuixTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = achievement.name,
                    style = MiuixTheme.textStyles.title2.copy(fontWeight = FontWeight.SemiBold),
                    color = MiuixTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = achievement.description,
                    style = MiuixTheme.textStyles.body1,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // XP Reward
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MiuixTheme.colorScheme.tertiaryContainer)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "XP +${achievement.xpReward}",
                        style = MiuixTheme.textStyles.title3.copy(fontWeight = FontWeight.Bold),
                        color = MiuixTheme.colorScheme.onTertiaryContainer
                    )
                    // Anda bisa menambahkan ikon kecil XP di sini jika mau
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Tutup
                Button( // Menggunakan Button dari Material 3 untuk konsistensi dialog
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MiuixTheme.colorScheme.primary
                    )
                ) {
                    Text("Awesome!", color = MiuixTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Achievement Unlocked Dialog Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Achievement Unlocked Dialog Dark")
@Composable
fun AchievementUnlockedDialogPreview() {
    BISBIAITheme {
        val sampleAchievement = AchievementEntity(
            id = "FIRST_SCAN_PREVIEW",
            name = "First Scan",
            description = "You've successfully completed your first image scan. Keep exploring!",
            icon = "🔍", // Emoji
            xpReward = 10,
            isUnlocked = true,
            requiredCount = 1
        )
        // Untuk preview dialog, kita bungkus dalam Box agar terlihat seperti dialog
        // dan berikan latar belakang yang sedikit berbeda dari dialognya agar terlihat jelas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MiuixTheme.colorScheme.outline.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            AchievementUnlockedDialog(
                achievement = sampleAchievement,
                onDismissRequest = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Achievement High XP Dialog")
@Composable
fun AchievementUnlockedHighXpDialogPreview() {
    BISBIAITheme {
        val sampleAchievementHighXp = AchievementEntity(
            id = "MASTER_EXPLORER_PREVIEW",
            name = "Master Explorer",
            description = "You have scanned over 100 unique objects!",
            icon = "🗺️", // Emoji
            xpReward = 50,
            isUnlocked = true,
            requiredCount = 100
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MiuixTheme.colorScheme.outline.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            AchievementUnlockedDialog(
                achievement = sampleAchievementHighXp,
                onDismissRequest = {}
            )
        }
    }
}