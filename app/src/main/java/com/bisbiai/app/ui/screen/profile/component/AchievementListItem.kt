// file: AchievementListItem.kt (di ui.screen.profile.component)
package com.bisbiai.app.ui.screen.profile.component

// import val goldBorder dari file sebelumnya jika masih dipakai, atau definisikan ulang
// val goldBorder = Color(0xFFFFD700)
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bisbiai.app.data.local.entity.AchievementEntity
import com.bisbiai.app.ui.theme.BISBIAITheme
import com.bisbiai.app.ui.theme.xpBlue
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AchievementListItem(
    achievement: AchievementEntity,
    modifier: Modifier = Modifier
) {
    val isUnlocked = achievement.isUnlocked
    val contentAlpha = if (isUnlocked) 1f else 0.6f // Konten lebih redup jika belum unlock

    val goldBorder = if (isSystemInDarkTheme()) Color(0xFFFFD700) else Color(0xFFB8860B) // Warna emas untuk border

    val borderModifier = if (isUnlocked) {
        Modifier.border(BorderStroke(1.dp, goldBorder), shape = RoundedCornerShape(12.dp)) // Border emas untuk yang sudah unlock
    } else {
        Modifier // Border biasa untuk yang belum
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(contentAlpha)
            .then(borderModifier),
        cornerRadius = 12.dp,
        color = MiuixTheme.colorScheme.surfaceContainer, // Warna card netral
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji sebagai Ikon di dalam Lingkaran
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp) // Ukuran lingkaran ikon
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) MiuixTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else MiuixTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
            ) {
                Text(
                    text = achievement.icon, // Emoji string
                    fontSize = 28.sp // Ukuran emoji
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.name,
                    style = MiuixTheme.textStyles.headline2.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isUnlocked) MiuixTheme.colorScheme.onSurface else MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
                Text(
                    text = achievement.description,
                    style = MiuixTheme.textStyles.body2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    modifier = Modifier.padding(top = 2.dp)
                )
                if (isUnlocked && achievement.unlockDate != null) {
                    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    Text(
                        text = "Earned: ${formatter.format(achievement.unlockDate)}",
                        style = MiuixTheme.textStyles.subtitle,
                        color = goldBorder,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // XP Reward (jika ada dan unlocked)
            if (isUnlocked) {
                Spacer(modifier = Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "+${achievement.xpReward}",
                        style = MiuixTheme.textStyles.title4.copy(fontWeight = FontWeight.Bold),
                        color = xpBlue // Atau warna lain yang sesuai
                    )
                    Text(
                        text = "XP",
                        style = MiuixTheme.textStyles.subtitle,
                        color = xpBlue.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AchievementListItemUnlockedPreview() {
    BISBIAITheme {
        AchievementListItem(
            achievement = AchievementEntity(
                "PREVIEW_UNLOCKED", "Super Scanner", "You scanned 10 items!", "🏆", 25, true, Date(), 10
            )
        )
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AchievementListItemLockedPreview() {
    BISBIAITheme {
        AchievementListItem(
            achievement = AchievementEntity(
                "PREVIEW_LOCKED", "Early Bird", "Login before 8 AM", "☀️", 5, false, null, 1
            )
        )
    }
}