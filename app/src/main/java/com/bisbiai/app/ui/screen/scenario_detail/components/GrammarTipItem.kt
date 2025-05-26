package com.bisbiai.app.ui.screen.scenario_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bisbiai.app.data.remote.dto.Example
import com.bisbiai.app.data.remote.dto.Tip
import com.bisbiai.app.ui.theme.xpGray
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun GrammarTipItem(
    tip: Tip,
    example: Example,
    onListenClick: () -> Unit,
    onXpChipClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min), // Memastikan anak-anak dapat mengisi tinggi dengan benar
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Garis oranye di sebelah kiri
        Box(
            modifier = Modifier
                .width(4.dp) // Lebar garis oranye, sesuaikan jika perlu
                .fillMaxHeight() // Mengisi tinggi Row
                .clip(
                    RoundedCornerShape(
                        topStart = 100.dp,
                        bottomStart = 100.dp
                    )
                ) // Sudut melengkung di sisi kiri
                .background(xpGray) // Warna garis oranye dari theme Anda
        )

        Card(
            modifier = Modifier
                .weight(1f)
                .clip(
                    RoundedCornerShape(
                        topEnd = 8.dp,
                        bottomEnd = 8.dp
                    )
                ), // Sudut melengkung di sisi kanan
            cornerRadius = 0.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top, // Agar XP Chip sejajar atas
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 8.dp)
                ) {
                    Text(
                        text = tip.en,
                        style = MiuixTheme.textStyles.headline2.copy(fontWeight = FontWeight.Medium),
                        color = MiuixTheme.colorScheme.onSurface
                    )
                    Text(
                        text = tip.id,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
//                HorizontalDivider(
//                    thickness = 0.5.dp,
//                    color = MiuixTheme.colorScheme.outline.copy(alpha = 0.5f)
//                )
//                Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 8.dp,
                        color = MiuixTheme.colorScheme.background
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = example.en,
                                style = MiuixTheme.textStyles.body1.copy(
                                    color = MiuixTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MiuixTheme.colorScheme.primary,
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                IconButton(
                                    onClick = onListenClick,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .offset(x = (-4).dp)
                                ) { // Offset kecil agar lebih rapi
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Listen to example: ${example.en}",
                                        tint = MiuixTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = "Listen",
                                    color = MiuixTheme.colorScheme.primary,
                                    style = MiuixTheme.textStyles.subtitle.copy(color = MiuixTheme.colorScheme.primary),
                                )
                            }
                            Text(
                                text = example.id,
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
                // XP Chip di luar Column teks agar bisa align ke top-end Row
                Box(modifier = Modifier.padding(top = 12.dp, end = 16.dp)) {
                    XpChip(
                        "+5 XP",
                        xpGray,
                        icon = Icons.Filled.WorkspacePremium,
                        onClick = onXpChipClick
                    )
                }
            }
        }
    }
}