package com.bisbiai.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import top.yukonga.miuix.kmp.theme.MiuixTheme


@Composable
fun BISBIAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MiuixTheme(
        colors = if (darkTheme) top.yukonga.miuix.kmp.theme.darkColorScheme() else top.yukonga.miuix.kmp.theme.lightColorScheme(),
        textStyles = textStyles(),
        content = content
    )
}