package com.bisbiai.app.ui.screen.scenario_detail.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bisbiai.app.data.remote.dto.Term
import com.bisbiai.app.ui.theme.xpBlue
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun VocabularyListItem(
    term: Term,
    onListenClick: () -> Unit,
    onXpChipClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, MiuixTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
        cornerRadius = 10.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = term.en,
                        style = MiuixTheme.textStyles.headline2.copy(fontWeight = FontWeight.Medium),
                        color = MiuixTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onListenClick, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen to ${term.en}",
                            tint = MiuixTheme.colorScheme.primary, // Warna ikon speaker
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = term.id,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
            }
            XpChip("+5 XP", xpBlue, onClick = onXpChipClick)
        }
    }
}
