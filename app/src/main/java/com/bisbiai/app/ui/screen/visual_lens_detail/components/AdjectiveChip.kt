package com.bisbiai.app.ui.screen.visual_lens_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bisbiai.app.data.remote.dto.RelatedAdjectivesItem
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AdjectiveChip(
    adjectiveItem: RelatedAdjectivesItem,
    onPlayAudio: (textToPlay: String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp)) // Chip-like rounded corners
            .background(CardDefaults.DefaultColor())
            .clickable { onPlayAudio(adjectiveItem.en) } // Click whole chip to play
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = adjectiveItem.en, // Assuming you only want to show English for adjectives
            fontSize = 14.sp,
            color = MiuixTheme.colorScheme.onSurface // Or a specific color for chips
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            // painter = painterResource(id = R.drawable.ic_listen),
            imageVector = Icons.Filled.VolumeUp,
            contentDescription = "Listen to adjective: ${adjectiveItem.en}",
            tint = MiuixTheme.colorScheme.primary, // Or a color that fits the chip
            modifier = Modifier.size(18.dp)
            // No separate clickable for icon here, entire chip is clickable
        )
    }
}