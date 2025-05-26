// Add this Composable, perhaps in the same file or a new components file
package com.bisbiai.app.ui.screen.home.components // Or your preferred components package

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.io.File

@Composable
fun ImageItem(
    imagePath: String, // You might change this to a URL or other image source later
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(File(imagePath))
            .allowHardware(false)
            .build()
    )
    Image(
        painter = painter,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(56.dp) // Adjust size as needed
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .border(1.dp, Color.White, RoundedCornerShape(20.dp)) // Optional border
    )
}