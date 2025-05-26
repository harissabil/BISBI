// file: AchievementsSection.kt (di ui.screen.profile.component)
package com.bisbiai.app.ui.screen.profile.component

// ... (import yang sudah ada)
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bisbiai.app.data.local.entity.AchievementEntity
import com.bisbiai.app.ui.screen.profile.AchievementFilterType
import com.bisbiai.app.ui.theme.BISBIAITheme
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.util.Date


@Composable
fun AchievementsSection(
    achievements: List<AchievementEntity>,
    currentFilter: AchievementFilterType,
    onFilterChanged: (AchievementFilterType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filteredAchievements = remember(achievements, currentFilter) {
        when (currentFilter) {
            AchievementFilterType.SHOW_ALL -> achievements
            AchievementFilterType.SHOW_EARNED -> achievements.filter { it.isUnlocked }
        }
    }
    var dropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        color = MiuixTheme.colorScheme.surfaceContainer // Warna card section
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header dengan Filter (Sama seperti sebelumnya)
            Row(
                modifier = Modifier.fillMaxWidth().animateContentSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Achievements",
                    style = MiuixTheme.textStyles.title2.copy(fontWeight = FontWeight.Bold),
                    color = MiuixTheme.colorScheme.onSurface
                )
                Box {
                    androidx.compose.material3.TextButton(onClick = { dropdownExpanded = true }) {
                        Text(
                            text = when (currentFilter) {
                                AchievementFilterType.SHOW_ALL -> "Show All"
                                AchievementFilterType.SHOW_EARNED -> "Show Earned"
                            },
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown, // Dari Material 3 Icons
                            contentDescription = "Change filter",
                            tint = MiuixTheme.colorScheme.primary
                        )
                    }
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.background(MiuixTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Show All", color = MiuixTheme.colorScheme.onSurface) },
                            onClick = {
                                onFilterChanged(AchievementFilterType.SHOW_ALL)
                                dropdownExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Show Earned",
                                    color = MiuixTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onFilterChanged(AchievementFilterType.SHOW_EARNED)
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MiuixTheme.colorScheme.outline.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // Konten Achievements
            if (filteredAchievements.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon( // Dari Material 3 Icons
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = if (currentFilter == AchievementFilterType.SHOW_EARNED) "No Badges Earned Yet!" else "No Achievements Available",
                        style = MiuixTheme.textStyles.title3.copy(fontWeight = FontWeight.Bold),
                        color = MiuixTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (currentFilter == AchievementFilterType.SHOW_EARNED) "Keep learning to unlock them!" else "Check back later for new challenges.",
                        style = MiuixTheme.textStyles.body1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Menggunakan LazyColumn untuk daftar vertikal
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    // Batasi tinggi LazyColumn jika ada konten lain di bawahnya dalam Column scrollable utama
                    // atau biarkan jika ini adalah akhir dari konten di ProfileScreen
                    // .heightIn(max = 400.dp) // Contoh batasan tinggi
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Jarak antar item
                ) {
                    items(filteredAchievements, key = { it.id }) { achievement ->
                        AchievementListItem(
                            achievement = achievement,
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Achievements Section Empty Earned")
@Composable
fun AchievementsSectionEmptyEarnedPreview() {
    BISBIAITheme {
        AchievementsSection(
            achievements = listOf(
                AchievementEntity(
                    "LOCKED_1",
                    "Super Learner",
                    "Learn 50 words",
                    "🧠",
                    30,
                    false,
                    null,
                    50
                )
            ),
            currentFilter = AchievementFilterType.SHOW_EARNED,
            onFilterChanged = {}
        )
    }
}

@Preview(showBackground = true, name = "Achievements Section With Items")
@Composable
fun AchievementsSectionWithItemsPreview() {
    val sampleAchievements = listOf(
        AchievementEntity(
            "UNLOCKED_1",
            "First Scan",
            "Completed your first scan",
            "🔍",
            10,
            true,
            Date(),
            1
        ),
        AchievementEntity("LOCKED_1", "Super Learner", "Learn 50 words", "🧠", 30, false, null, 50),
        AchievementEntity(
            "UNLOCKED_2",
            "Scenario Ace",
            "Mastered a scenario",
            "🎭",
            20,
            true,
            Date(),
            1
        )
    )
    BISBIAITheme {
        AchievementsSection(
            achievements = sampleAchievements,
            currentFilter = AchievementFilterType.SHOW_ALL,
            onFilterChanged = {}
        )
    }
}