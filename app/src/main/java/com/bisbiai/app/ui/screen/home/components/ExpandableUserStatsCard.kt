package com.bisbiai.app.ui.screen.home.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun ExpandableUserStatsCard(
    currentLevel: Int,
    currentXp: Int,
    xpToNextLevel: Int,
    dayStreak: Int,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val progress = if (xpToNextLevel > 0) currentXp.toFloat() / xpToNextLevel.toFloat() else 0f

    // Colors (consistent with the original image)
    val primaryBlue = Color(0xFF3F51B5) // Main card background
    val lightBlueBackground = Color(0xFF5C6BC0) // Background for star icon circle
    val starYellow = Color(0xFFFFEB3B) // Star icon color
    val fireOrange = Color(0xFFFFA726) // Fire icon color
    val textColorWhite = Color.White
    val textColorLight = Color.White.copy(alpha = 0.8f) // For secondary text

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }, // Click to toggle expansion
        cornerRadius = 20.dp,
        color = primaryBlue,
    ) {
        AnimatedContent(targetState = isExpanded) {
            if (it) {
                FullUserStatsContent(
                    currentLevel = currentLevel,
                    currentXp = currentXp,
                    xpToNextLevel = xpToNextLevel,
                    dayStreak = dayStreak,
                    progress = progress,
                    lightBlueBackground = lightBlueBackground,
                    starYellow = starYellow,
                    fireOrange = fireOrange,
                    textColor = textColorWhite,
                    textColorLight = textColorLight,
                )
            } else {
                CompactUserStatsContent(
                    currentLevel = currentLevel,
                    progress = progress,
                    lightBlueBackground = lightBlueBackground,
                    starYellow = starYellow,
                    textColor = textColorWhite,
                    expandIconTint = textColorWhite
                )
            }
        }
    }
}

@Composable
fun FullUserStatsContent(
    currentLevel: Int,
    currentXp: Int,
    xpToNextLevel: Int,
    dayStreak: Int,
    progress: Float,
    lightBlueBackground: Color,
    starYellow: Color,
    fireOrange: Color,
    textColor: Color,
    textColorLight: Color,
) {
    val percentageProgress = (progress * 100).toInt()

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Row: Level and Streak
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Level Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(lightBlueBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Level Star",
                        tint = starYellow,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "LEVEL",
                        color = textColorLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$currentLevel",
                        color = textColor,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 40.sp
                    )
                }
            }

            // Streak Info
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "Day Streak",
                        tint = fireOrange,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$dayStreak",
                        color = textColor,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 40.sp
                    )
                }
                Text(
                    text = "DAY STREAK!",
                    color = textColorLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // XP Text above progress bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "XP: $currentXp/$xpToNextLevel",
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$percentageProgress%",
                color = textColor,
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
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            colors = ProgressIndicatorDefaults.progressIndicatorColors(backgroundColor = Color.White)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Text below progress bar
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

@Composable
fun CompactUserStatsContent(
    currentLevel: Int,
    progress: Float,
    lightBlueBackground: Color,
    starYellow: Color,
    textColor: Color,
    expandIconTint: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp), // Slightly reduced padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Level Info (smaller)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.wrapContentWidth() // Prevent this Row from taking too much space
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp) // Smaller icon container
                    .clip(CircleShape)
                    .background(lightBlueBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Level Star",
                    tint = starYellow,
                    modifier = Modifier.size(24.dp) // Smaller star icon
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$currentLevel",
                color = textColor,
                fontSize = 24.sp, // Smaller level text
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp)) // Space between level and progress bar

        // Progress Bar
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .weight(1f) // Allow progress bar to fill available space
                .height(10.dp) // Slightly thinner progress bar
                .clip(RoundedCornerShape(5.dp)),
            colors = ProgressIndicatorDefaults.progressIndicatorColors(backgroundColor = Color.White)
        )

        Spacer(modifier = Modifier.width(16.dp)) // Space between progress bar and icon

        // Expand Icon
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = "Expand",
            tint = expandIconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}


@Preview(showBackground = true, name = "Compact State Preview")
@Composable
fun CompactUserStatsCardPreview() {
    BISBIAITheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ExpandableUserStatsCard( // Initially compact
                currentLevel = 2,
                currentXp = 25,
                xpToNextLevel = 150,
                dayStreak = 1 // Matches original image's streak
            )
        }
    }
}

@Preview(showBackground = true, name = "Expanded State - Initial Data")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Expanded State - Initial Data (Night)"
)
@Composable
fun UserStatsCardPreview() { // Renamed from UserStatsCardPreview to avoid conflict if needed, or keep as is.
    // This will show compact by default. To see expanded, you'd need to interact or set initial state.
    // For a direct preview of expanded, we might need a helper.
    // Or, we can just rely on clicking in the interactive preview.
    BISBIAITheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ExpandableUserStatsCard(
                currentLevel = 2,
                currentXp = 25,
                xpToNextLevel = 150,
                dayStreak = 1 // Match the image streak
            )
        }
    }
}


@Preview(showBackground = true, name = "Expanded State - Almost Full XP")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Expanded State - Almost Full XP (Night)"
)
@Composable
fun UserStatsCardAlmostFullPreview() {
    BISBIAITheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ExpandableUserStatsCard(
                currentLevel = 5,
                currentXp = 140,
                xpToNextLevel = 150,
                dayStreak = 32
            )
        }
    }
}

// To specifically preview the expanded state directly, you could create a preview
// that sets `isExpanded` to true initially, but this requires modifying the ExpandableUserStatsCard
// to accept an initial `expanded` parameter, or creating a wrapper.
// For now, interactive preview clicking will show the expanded state.