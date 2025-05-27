package com.bisbiai.app.ui.screen.scenario_detail.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bisbiai.app.data.remote.dto.ScenarioTitle
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun ScenarioTitleItem(
    title: ScenarioTitle,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .border(1.dp, MiuixTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
        cornerRadius = 12.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title.en,
                style = MiuixTheme.textStyles.title4.copy(fontWeight = FontWeight.Bold),
                color = MiuixTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title.id,
                style = MiuixTheme.textStyles.body1.copy(fontStyle = FontStyle.Italic),
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
        }
    }
}