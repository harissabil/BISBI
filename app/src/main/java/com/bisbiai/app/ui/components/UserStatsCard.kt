package com.bisbiai.app.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
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
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.ProgressIndicatorDefaults
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun UserStatsCard(
    currentLevel: Int,
    currentXp: Int,
    xpToNextLevel: Int,
    dayStreak: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (xpToNextLevel > 0) currentXp.toFloat() / xpToNextLevel.toFloat() else 0f
    val percentageProgress = (progress * 100).toInt()

    // Warna utama dari gambar (biru tua)
    val primaryBlue = Color(0xFF3F51B5) // Mirip Indigo 500
    val lightBlueBackground = Color(0xFF5C6BC0) // Biru sedikit lebih terang untuk bg ikon
    val starYellow = Color(0xFFFFEB3B) // Kuning untuk bintang
    val fireOrange = Color(0xFFFFA726) // Oranye untuk api
//    val progressTrackColor = Color.LightGray.copy(alpha = 0.4f)
    val textColorLight = Color.White.copy(alpha = 0.8f)

    Card(
        modifier = modifier
            .fillMaxWidth(),
        cornerRadius = 20.dp, // Sudut lebih bulat seperti gambar
        color = primaryBlue,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp) // Padding internal
        ) {
            // Baris Atas: Level dan Streak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top // Agar teks "DAY STREAK!" sejajar dengan bawah ikon api
            ) {
                // Info Level
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp) // Ukuran lingkaran ikon
                            .clip(CircleShape)
                            .background(lightBlueBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Level Star",
                            tint = starYellow,
                            modifier = Modifier.size(40.dp) // Ukuran ikon bintang
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "LEVEL",
                            color = textColorLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium // Sedikit tebal
                        )
                        Text(
                            text = "$currentLevel",
                            color = Color.White,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 40.sp // Mengurangi spasi bawah default
                        )
                    }
                }

                // Info Streak
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = "Day Streak",
                            tint = fireOrange,
                            modifier = Modifier.size(40.dp) // Ukuran ikon api
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$dayStreak",
                            color = Color.White,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 40.sp
                        )
                    }
                    Text(
                        text = "DAY STREAK!",
                        color = textColorLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium // Sedikit tebal
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // XP Text di atas progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom // Agar persentase sejajar bawah
            ) {
                Text(
                    text = "XP: $currentXp/$xpToNextLevel",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$percentageProgress%",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp) // Ketinggian progress bar
                    .clip(RoundedCornerShape(6.dp)), // Sudut bulat untuk progress bar
                colors = ProgressIndicatorDefaults.progressIndicatorColors(backgroundColor = Color.White)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Teks di bawah progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$currentXp / $xpToNextLevel XP",
                    color = textColorLight,
                    fontSize = 12.sp
                )
                Text(
                    text = "$percentageProgress% to Next Level",
                    color = textColorLight,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun UserStatsCardPreview() {
    BISBIAITheme { // Bungkus dengan MaterialTheme untuk styling default jika diperlukan
        Box(modifier = Modifier.padding(16.dp)) { // Menambahkan padding di sekitar card untuk preview
            UserStatsCard(
                currentLevel = 2,
                currentXp = 25,
                xpToNextLevel = 150,
                dayStreak = 0
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun UserStatsCardAlmostFullPreview() {
    BISBIAITheme {
        Box(modifier = Modifier.padding(16.dp)) {
            UserStatsCard(
                currentLevel = 5,
                currentXp = 140,
                xpToNextLevel = 150,
                dayStreak = 32
            )
        }
    }
}