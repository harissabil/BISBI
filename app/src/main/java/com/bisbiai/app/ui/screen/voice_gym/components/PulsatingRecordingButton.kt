package com.bisbiai.app.ui.screen.voice_gym.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Icon

@Composable
fun PulsatingRecordingButton(
    modifier: Modifier = Modifier,
    isRecording: Boolean,
    baseColor: Color,
    pulseColor: Color,
    onClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsatingButton")
    val pulseFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isRecording) 1f else 0f, // Hanya berdenyut saat isRecording
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = "pulseFraction"
    )

    val outerCircleColor =
        if (isRecording) pulseColor.copy(alpha = 0.5f * (1 - pulseFraction)) else baseColor.copy(
            alpha = 0.2f
        )
    val middleCircleColor =
        if (isRecording) pulseColor.copy(alpha = 0.7f) else baseColor.copy(alpha = 0.5f)
    val innerCircleColor = if (isRecording) pulseColor else baseColor

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(120.dp)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        // Lingkaran terluar (denyut)
        if (isRecording) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val radius = size.minDimension / 2 * pulseFraction
                drawCircle(
                    color = pulseColor.copy(alpha = 0.3f * (1 - pulseFraction)),
                    radius = radius,
                    center = center
                )
            }
        }

        // Lingkaran luar statis (atau dasar denyut)
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(outerCircleColor)
        )
        // Lingkaran tengah
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(middleCircleColor)
        )
        // Lingkaran dalam (inti)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(innerCircleColor)
        ) {
            Icon(
                imageVector = Icons.Filled.Mic,
                contentDescription = "Record",
                tint = Color.White, // Ikon mic selalu putih
                modifier = Modifier.size(36.dp)
            )
        }
    }
}