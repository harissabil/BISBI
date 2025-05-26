package com.bisbiai.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.bisbiai.app.R
import com.bisbiai.app.ui.theme.AppFont.poppins
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.TextStyles

object AppFont {
    val poppins = FontFamily(
        Font(R.font.pregular),
        Font(R.font.pitalic, style = FontStyle.Italic),
        Font(R.font.pmedium, FontWeight.Medium),
        Font(R.font.pmedium_italic, FontWeight.Medium, style = FontStyle.Italic),
        Font(R.font.psemibold, FontWeight.SemiBold),
        Font(R.font.psemibold_italic, FontWeight.SemiBold, style = FontStyle.Italic),
        Font(R.font.psemibold, FontWeight.Bold),
        Font(R.font.psemibold_italic, FontWeight.Bold, style = FontStyle.Italic),
        Font(R.font.plight, FontWeight.Light)
    )
}

@Composable
fun textStyles(): TextStyles {
    return TextStyles(
        main = MiuixTheme.textStyles.main.copy(fontFamily = poppins),
        paragraph = MiuixTheme.textStyles.paragraph.copy(fontFamily = poppins),
        body1 = MiuixTheme.textStyles.body1.copy(fontFamily = poppins),
        body2 = MiuixTheme.textStyles.body2.copy(fontFamily = poppins),
        button = MiuixTheme.textStyles.button.copy(fontFamily = poppins),
        footnote1 = MiuixTheme.textStyles.footnote1.copy(fontFamily = poppins),
        footnote2 = MiuixTheme.textStyles.footnote2.copy(fontFamily = poppins),
        headline1 = MiuixTheme.textStyles.headline1.copy(fontFamily = poppins),
        headline2 = MiuixTheme.textStyles.headline2.copy(fontFamily = poppins),
        subtitle = MiuixTheme.textStyles.subtitle.copy(fontFamily = poppins),
        title1 = MiuixTheme.textStyles.title1.copy(fontFamily = poppins),
        title2 = MiuixTheme.textStyles.title2.copy(fontFamily = poppins),
        title3 = MiuixTheme.textStyles.title3.copy(fontFamily = poppins),
        title4 = MiuixTheme.textStyles.title4.copy(fontFamily = poppins),
    )
}