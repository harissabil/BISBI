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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bisbiai.app.data.remote.dto.Phrase
import com.bisbiai.app.ui.theme.xpOrange
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun KeyPhraseListItem(
    phrase: Phrase,
    onListenClick: () -> Unit,
    onXpChipClick: () -> Unit
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
                .clip(RoundedCornerShape(topStart = 100.dp, bottomStart = 100.dp)) // Sudut melengkung di sisi kiri
                .background(xpOrange) // Warna garis oranye dari theme Anda
        )

        // Card asli Anda, tanpa border sendiri
        Card(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)), // Sudut melengkung di sisi kanan
            cornerRadius = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = phrase.en,
                            style = MiuixTheme.textStyles.headline2.copy(fontWeight = FontWeight.Medium),
                            color = MiuixTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = onListenClick, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Listen to ${phrase.en}",
                                tint = MiuixTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = phrase.id,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                }
                // Asumsikan XpChip adalah Composable yang sudah Anda miliki
                // XpChip("+5 XP", xpOrange)
                // Untuk placeholder jika XpChip belum ada:
                XpChip("+5 XP", xpOrange, onClick = onXpChipClick)
            }
        }
    }
}
