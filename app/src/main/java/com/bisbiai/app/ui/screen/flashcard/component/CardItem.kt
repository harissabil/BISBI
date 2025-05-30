package com.bisbiai.app.ui.screen.flashcard.component

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bisbiai.app.domain.model.Flashcard
import com.bisbiai.app.ui.theme.BISBIAITheme
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun CardItem(
    modifier: Modifier = Modifier,
    flashcard: Flashcard,
) {

    var rotated by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (rotated) 180f else 0f,
        animationSpec = tween(500),
        label = "rotation"
    )

    val animateFront by animateFloatAsState(
        targetValue = if (!rotated) 1f else 0f,
        animationSpec = tween(500),
        label = "animateFront"
    )

    val animateBack by animateFloatAsState(
        targetValue = if (rotated) 1f else 0f,
        animationSpec = tween(500),
        label = "animateBack"
    )

    Card(
        modifier = modifier
            .height(220.dp)
            .fillMaxWidth()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density
            }
            .clickable {
                rotated = !rotated
            },
        cornerRadius = 14.dp,
    ) {
        if (!rotated) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MiuixTheme.colorScheme.primaryContainer)
                    .graphicsLayer {
                        alpha = animateFront
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = flashcard.objectName,
                    color = MiuixTheme.colorScheme.onPrimaryContainer,
                    style = MiuixTheme.textStyles.title2,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MiuixTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 2.dp,
                        color = MiuixTheme.colorScheme.outline,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .graphicsLayer {
                        alpha = animateBack
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = flashcard.translation,
                    color = MiuixTheme.colorScheme.onSurface,
                    style = MiuixTheme.textStyles.title2,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = animateBack
                            rotationY = rotation
                        }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CardItemPreview() {
    BISBIAITheme {
        CardItem(
            flashcard = Flashcard(
                objectName = "Kucing",
                translation = "Cat",
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}