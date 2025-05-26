package com.bisbiai.app.ui.screen.profile.component// package com.bisbiai.app.ui.components (atau di mana Anda menyimpannya)
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator // Ganti dari Miuix ke Material 3 untuk konsistensi warna
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bisbiai.app.ui.theme.BISBIAITheme
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme


@Composable
fun CompactUserStats(
    // Ganti nama agar tidak bentrok jika UserStatsCard lama masih dipakai
    modifier: Modifier = Modifier,
    currentLevel: Int,
    currentXp: Int,
    xpToNextLevel: Int,
    dayStreak: Int,
    onPrimaryColor: Color, // Warna teks utama di atas background primary
    onPrimaryVariantColor: Color, // Warna teks sekunder/lebih redup
    progressColor: Color, // Warna untuk progress bar aktif
    progressTrackColor: Color, // Warna untuk track progress bar
) {
    val progress = if (xpToNextLevel > 0) currentXp.toFloat() / xpToNextLevel.toFloat() else 0f
    val percentageProgress = (progress * 100).toInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp) // Tambahkan padding atas untuk jarak dari info profil
    ) {
        // Baris Atas: Level dan Streak (lebih sederhana)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Info Level
            Column {
                Text(
                    text = "LEVEL",
                    color = onPrimaryVariantColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$currentLevel",
                    color = onPrimaryColor,
                    fontSize = 32.sp, // Sedikit lebih kecil dari desain asli
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp
                )
            }

            // Info Streak
            Column(horizontalAlignment = Alignment.End) { // Rata kanan
                Text(
                    text = "DAY STREAK", // Teks lebih sederhana
                    color = onPrimaryVariantColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$dayStreak",
                    color = onPrimaryColor,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // XP Text di atas progress bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "XP: $currentXp/$xpToNextLevel",
                color = onPrimaryColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$percentageProgress%",
                color = onPrimaryColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Progress Bar menggunakan Material 3 untuk kontrol warna yang lebih baik
        LinearProgressIndicator(
            progress = { progress }, // Menggunakan lambda untuk Material 3 versi baru
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp) // Sedikit lebih tipis
                .clip(RoundedCornerShape(5.dp)),
            color = progressColor, // Warna progress aktif
            trackColor = progressTrackColor, // Warna track (bagian yang belum terisi)
        )


        Spacer(modifier = Modifier.height(6.dp))

        // Teks di bawah progress bar (opsional, jika masih diperlukan)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$currentXp / $xpToNextLevel XP",
                color = onPrimaryVariantColor,
                fontSize = 12.sp
            )
            Text(
                text = "$percentageProgress% to Next Level",
                color = onPrimaryVariantColor,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompactUserStatsPreview() {
    BISBIAITheme {
        Box(
            modifier = Modifier
                .background(MiuixTheme.colorScheme.primary) // Latar belakang biru untuk preview
                .padding(16.dp)
        ) {
            CompactUserStats(
                currentLevel = 5,
                currentXp = 140,
                xpToNextLevel = 150,
                dayStreak = 32,
                onPrimaryColor = MiuixTheme.colorScheme.onPrimary,
                onPrimaryVariantColor = MiuixTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                progressColor = Color.White, // Misalnya progress bar putih di atas biru
                progressTrackColor = Color.White.copy(alpha = 0.3f) // Track lebih redup
            )
        }
    }
}