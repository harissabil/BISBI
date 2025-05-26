package com.bisbiai.app.ui.screen.scenario_detail.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MiuixTheme.textStyles.title4.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}