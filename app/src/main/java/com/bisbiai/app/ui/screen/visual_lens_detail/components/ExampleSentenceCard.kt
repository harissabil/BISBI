package com.bisbiai.app.ui.screen.visual_lens_detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bisbiai.app.data.remote.dto.ExampleSentencesItem
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun ExampleSentenceCard(
    sentenceItem: ExampleSentencesItem,
    onPlayAudio: (textToPlay: String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = CardDefaults.DefaultColor(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = sentenceItem.en,
                    fontSize = 16.sp,
                    color = MiuixTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    // painter = painterResource(id = R.drawable.ic_listen),
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Listen to sentence: ${sentenceItem.en}",
                    tint = MiuixTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onPlayAudio(sentenceItem.en) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = sentenceItem.id,
                fontSize = 16.sp,
                color = MiuixTheme.colorScheme.onSurfaceSecondary
            )
        }
    }
}