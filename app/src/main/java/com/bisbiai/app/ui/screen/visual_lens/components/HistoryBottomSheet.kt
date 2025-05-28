package com.bisbiai.app.ui.screen.visual_lens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.bisbiai.app.core.utils.toDdMmYyyyHhMmSs
import com.bisbiai.app.data.local.entity.DetectedObjectEntity
import com.bisbiai.app.data.local.relation.ObjectWithDetails
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryBottomSheet(
    modifier: Modifier = Modifier,
    historyItems: List<ObjectWithDetails>,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
    onDismissRequest: () -> Unit,
    onClick: (DetectedObjectEntity) -> Unit,
    onDeleteClick: (DetectedObjectEntity) -> Unit
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        containerColor = MiuixTheme.colorScheme.surfaceContainer, // Warna background bottom sheet
        dragHandle = { // Custom drag handle untuk estetika lebih
            BottomSheetDefaults.DragHandle()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp) // Padding bawah untuk konten
        ) {
            Text(
                text = "Detection History",
                style = MiuixTheme.textStyles.title3,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp, top = 8.dp) // Padding atas jika tidak ada drag handle custom yang tinggi
            )

            if (historyItems.isEmpty()) {
                Text(
                    text = "No detection history available.",
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 32.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = historyItems, key = { it.detectedObject.id }) { item ->
                        HistoryItem(
                            detectedObject = item.detectedObject,
                            onDeleteClick = { onDeleteClick(item.detectedObject) },
                            onClick = { detectedObject ->
                                onClick(detectedObject)
                            },
                        )
                        if (historyItems.last() != item) { // Tambahkan Divider kecuali untuk item terakhir
                            HorizontalDivider(
                                modifier = Modifier.padding(top = 12.dp),
                                thickness = 0.5.dp,
                                color = MiuixTheme.colorScheme.outline.copy(alpha = 0.75f)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp)) // Spacer di akhir
        }
    }
}

@Composable
fun HistoryItem(
    modifier: Modifier = Modifier,
    detectedObject: DetectedObjectEntity,
    onClick: (detectedObject: DetectedObjectEntity) -> Unit,
    onDeleteClick: () -> Unit
) {
    val objectNames = remember(detectedObject.detectObjects.predictions) {
        detectedObject.detectObjects.predictions.joinToString(", ") { it.objectName }.ifEmpty { "No objects detected" }
    }
    val formattedTimestamp = remember(detectedObject.timestamp) {
        detectedObject.timestamp.toDdMmYyyyHhMmSs()
    }

    Card(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable { onClick(detectedObject) },
        cornerRadius = 12.dp,
        color = MiuixTheme.colorScheme.surfaceContainer, // Warna card
//        onClick = { onClick(detectedObject) } // Aksi saat item diklik
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(detectedObject.imagePath)
                    .crossfade(true)
                    .build(),
                contentDescription = "Gambar ${objectNames.take(20)}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant), // Background saat loading/error
                loading = {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 2.dp,
                    )
                },
                error = {
                    Icon(
                        imageVector = Icons.Outlined.ImageNotSupported,
                        contentDescription = "Gagal memuat gambar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = objectNames,
                    style = MiuixTheme.textStyles.title4,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MiuixTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formattedTimestamp,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Hapus riwayat",
                    tint = MaterialTheme.colorScheme.error // Warna ikon hapus
                )
            }
        }
    }
}