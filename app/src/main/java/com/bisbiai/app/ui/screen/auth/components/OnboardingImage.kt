package com.bisbiai.app.ui.screen.auth.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun OnboardingImage(
    modifier: Modifier = Modifier,
    @DrawableRes painterId: Int,
) {
    Box(modifier = modifier) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.8f),
            painter = painterResource(painterId),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}