package com.bisbiai.app.ui.screen.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bisbiai.app.data.local.relation.ObjectWithDetails
import top.yukonga.miuix.kmp.basic.FloatingActionButton
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun ExpandableImageSelector(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    items: List<ObjectWithDetails>,
    onItemClick: (ObjectWithDetails) -> Unit,
) {
    val primaryBlue = Color(0xFF3F51B5)

    val itemSize = 56.dp
    val verticalPadding = 8.dp
    val rowHeight = itemSize + (verticalPadding * 2) // 56dp + 8dp + 8dp = 72dp

    Box(modifier = modifier.padding(start = 16.dp)) {
        // Expanded State: Horizontal list with a collapse button
        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInHorizontally { fullWidth -> fullWidth } + expandHorizontally(expandFrom = Alignment.End),
            exit = shrinkHorizontally(
                animationSpec = spring( // Menggunakan spring
                    dampingRatio = Spring.DampingRatioNoBouncy, // Kurangi pantulan saat menutup
                    stiffness = Spring.StiffnessMedium
                ),
                shrinkTowards = Alignment.End
            ) + fadeOut(animationSpec = tween()) // Konten fade out bersamaan
        ) {
            Row(
                modifier = Modifier
                    .background(
                        MiuixTheme.colorScheme.primary.copy(alpha = 0.75f), // Semi-transparent background
                        RoundedCornerShape(
                            topStart = 24.dp,
                            bottomStart = 24.dp
                        ) // Rounded only on the left
                    )
                    .padding(
                        start = 12.dp,
                        top = 8.dp,
                        bottom = 8.dp,
                        end = 8.dp
                    ) // No end padding to be flush
                    .height(rowHeight), // Make row height determined by its content
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(items, key = { it.detectedObject.id }) { item ->
                        ImageItem(
                            imagePath = item.detectedObject.imagePath,
                            contentDescription = item.detectedObject.id.toString(),
                            onClick = { onItemClick(item) }
                        )
                    }
                }
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight, // Icon to collapse (points towards the hidden part)
                        contentDescription = "Collapse Selector",
                        tint = Color.White // Icon color
                    )
                }
            }
        }

        // Collapsed State: FAB or a simple button
        AnimatedVisibility(
            visible = !isExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FloatingActionButton(
                onClick = onToggle,
                containerColor = MiuixTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.Layers, // Or Icons.Filled.ChevronLeft
                    contentDescription = "Expand Selector",
                    tint = Color.White // Icon color
                )
            }
        }
    }
}