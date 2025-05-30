package com.bisbiai.app.ui.screen.flashcard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun ProgressIndicator(
    modifier: Modifier = Modifier,
    current: String,
    progress: () -> Float,
) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = current,
            color = MiuixTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp
        )
        Box(
            modifier = Modifier.border(
                width = 2.dp,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                shape = CircleShape
            )
        ) {
//            LinearProgressIndicator(
//                progress = progress,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(28.dp)
//                    .padding(6.dp),
//                color = Green700,
//                trackColor = MaterialTheme.colorScheme.background,
//                strokeCap = StrokeCap.Round
//            )
            StripedProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .padding(8.dp),
                progress = progress(),
            )
        }
    }
}

@Composable
fun StripedProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    stripeColor: Color = MiuixTheme.colorScheme.primary,
    stripeColorSecondary: Color = MiuixTheme.colorScheme.secondary,
    backgroundColor: Color = Color.Transparent,
    clipShape: Shape = RoundedCornerShape(16.dp)
) {
    Box(
        modifier = modifier
            .clip(clipShape)
            .background(backgroundColor)
            .height(10.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(clipShape)
                .background(createStripeBrush(stripeColor, stripeColorSecondary, 7.dp))
                .fillMaxHeight()
                .fillMaxWidth(progress)
        )
    }
}

@Composable
private fun createStripeBrush(
    stripeColor: Color,
    stripeBg: Color,
    stripeWidth: Dp
): Brush {
    val stripeWidthPx = with(LocalDensity.current) { stripeWidth.toPx() }
    val brushSizePx = 2 * stripeWidthPx
    val stripeStart = stripeWidthPx / brushSizePx

    return Brush.linearGradient(
        stripeStart to stripeBg,
        stripeStart to stripeColor,
        start = Offset(0f, 0f),
        end = Offset(brushSizePx, brushSizePx),
        tileMode = TileMode.Repeated
    )
}