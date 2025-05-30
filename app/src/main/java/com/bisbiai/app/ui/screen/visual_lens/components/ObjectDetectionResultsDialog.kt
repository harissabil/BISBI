package com.bisbiai.app.ui.screen.visual_lens.components

import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.bisbiai.app.data.remote.dto.DetectObjectItem
import com.bisbiai.app.data.remote.dto.DetectObjectsResponse
import com.bisbiai.app.ui.components.FullScreenLoading
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.io.File

@OptIn(ExperimentalTextApi::class)
@Composable
fun ObjectDetectionResultsDialog(
    imageFile: File,
    detectionResult: DetectObjectsResponse,
    originalImageWidth: Int,
    originalImageHeight: Int,
    isLoading: Boolean,
    onDismissRequest: () -> Unit,
    onObjectClick: (DetectObjectItem) -> Unit,
) {
    val painter = rememberAsyncImagePainter(model = imageFile)

    // Menggunakan warna dari MiuixTheme
    val primaryColor = MiuixTheme.colorScheme.primary
    val onPrimaryColor = MiuixTheme.colorScheme.onPrimary // Warna teks di atas primary
    val surfaceColor = MiuixTheme.colorScheme.surface
    val onSurfaceColor = MiuixTheme.colorScheme.onSurface // Warna teks di atas surface

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false), // Allows custom width
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f) // Menggunakan sebagian besar lebar layar tapi tidak penuh
                .padding(vertical = 32.dp),
            shape = RoundedCornerShape(28.dp), // Sudut lebih besar untuk tampilan modern
            color = surfaceColor,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Object Detection Results", // "Object Detection Results"
                    style = MiuixTheme.textStyles.title3.copy(color = onSurfaceColor), // Gunakan typography dari Miuix jika ada, atau MaterialTheme
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                if (isLoading && detectionResult.predictions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(originalImageWidth / originalImageHeight.toFloat())
                            .height(250.dp), // Ketinggian sementara untuk loader
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = primaryColor,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(originalImageWidth / originalImageHeight.toFloat())
                            .clip(RoundedCornerShape(12.dp)) // Clip image for rounded corners
                            .pointerInput(detectionResult) {
                                detectTapGestures { offset ->
                                    val scaleX = size.width / originalImageWidth.toFloat()
                                    val scaleY = size.height / originalImageHeight.toFloat()

                                    detectionResult.predictions.forEach { result ->
                                        val rect = result.boundingBox
                                        val scaledRect = RectF(
                                            rect.x * scaleX,
                                            rect.y * scaleY,
                                            (rect.x + rect.width) * scaleX,
                                            (rect.y + rect.height) * scaleY
                                        )
                                        if (offset.x in scaledRect.left..scaledRect.right &&
                                            offset.y in scaledRect.top..scaledRect.bottom
                                        ) {
                                            onObjectClick(result)
                                        }
                                    }
                                }
                            }
                            .drawWithContent {
                                drawContent() // Draw image first

                                val scaleX = size.width / originalImageWidth.toFloat()
                                val scaleY = size.height / originalImageHeight.toFloat()
                                val cornerRadiusPx = 8.dp.toPx() // Radius untuk sudut kotak

                                detectionResult.predictions.forEach { result ->
                                    val rect = result.boundingBox
                                    val left = rect.x * scaleX
                                    val top = rect.y * scaleY
                                    val boxWidth = rect.width * scaleX
                                    val boxHeight = rect.height * scaleY
                                    val right = left + boxWidth
                                    val bottom = top + boxHeight

                                    val boundingBoxRect = RectF(left, top, right, bottom)

                                    drawIntoCanvas { canvas ->
                                        // Bounding Box
                                        val boxPaint = Paint().apply {
                                            color = primaryColor.toArgb()
                                            style = Paint.Style.STROKE
                                            strokeWidth = 6f
                                            isAntiAlias = true
                                        }
                                        canvas.nativeCanvas.drawRoundRect(
                                            boundingBoxRect,
                                            cornerRadiusPx,
                                            cornerRadiusPx,
                                            boxPaint
                                        )

                                        // Label Background
                                        val labelHeight = 38f
                                        val labelRect = RectF(
                                            left,
                                            top - labelHeight,
                                            right,
                                            top
                                        )
                                        val labelBgPaint = Paint().apply {
                                            color = primaryColor.toArgb()
                                            style = Paint.Style.FILL
                                            isAntiAlias = true
                                        }
                                        // Gambar kotak label dengan sudut atas membulat
                                        val path = android.graphics.Path()
                                        path.addRoundRect(labelRect,
                                            floatArrayOf(cornerRadiusPx, cornerRadiusPx, cornerRadiusPx, cornerRadiusPx, 0f, 0f, 0f, 0f),
                                            android.graphics.Path.Direction.CW)
                                        canvas.nativeCanvas.drawPath(path, labelBgPaint)


                                        // Label Text
                                        val textPaint = Paint().apply {
                                            color = onPrimaryColor.toArgb()
                                            textSize = 28f // Slightly larger text
                                            isAntiAlias = true
                                            textAlign = Paint.Align.LEFT
                                        }
                                        canvas.nativeCanvas.drawText(
                                            // confidence is in double, convert to integer percentage
                                            result.objectName + " (${(result.confidence * 100).toInt()}%)",
                                            left + 8f, // Padding kiri untuk teks
                                            top - (labelHeight / 2) + (textPaint.descent() - textPaint.ascent()) / 2 - textPaint.descent() -2f , // Center text vertically
                                            textPaint
                                        )
                                    }
                                }
                            }
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = "Detected Image", // "Detected Image"
                            contentScale = ContentScale.FillBounds, // Or FillWidth/FillHeight depending on desired behavior
                            modifier = Modifier.matchParentSize()
                        )

                        if (isLoading) {
                            FullScreenLoading()
                        }
                    }

                    if (!isLoading && detectionResult.predictions.isEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No objects detected in this image.", // "No objects detected in this image."
                            style = MiuixTheme.textStyles.body1.copy(color = onSurfaceColor.copy(alpha = 0.7f)),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tap bounding box for object details.",
                        style = MiuixTheme.textStyles.body1.copy(color = onSurfaceColor.copy(alpha = 0.7f)),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    )
                }


                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismissRequest,
                        text = "Close" // "Close"
                    )
                    // Jika Anda menggunakan Miuix TextButton, pastikan propertinya sesuai
                    // contoh: top.yukonga.miuix.kmp.widget.TextButton(onClick = onDismissRequest) { Text("Tutup") }
                }
            }
        }
    }
}

// Fungsi sizeRatio sepertinya tidak digunakan dalam ObjectDetectionResultsDialog,
// jadi saya akan biarkan atau Anda bisa menghapusnya jika memang tidak relevan.
// private fun sizeRatio(originalWidth: Int, originalHeight: Int): Float {
//     val maxDimension = maxOf(originalWidth, originalHeight)
//     return 1f / maxDimension
// }