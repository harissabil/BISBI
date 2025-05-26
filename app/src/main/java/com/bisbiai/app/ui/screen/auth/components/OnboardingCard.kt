package com.bisbiai.app.ui.screen.auth.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bisbiai.app.ui.theme.BISBIAITheme
import com.bisbiai.app.ui.theme.spacing
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun OnboardingCard(
    title: String,
    description: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        colors = CardColors(
            containerColor = MiuixTheme.colorScheme.surface,
            contentColor = MiuixTheme.colorScheme.onSurface,
            disabledContainerColor = MiuixTheme.colorScheme.surfaceVariant,
            disabledContentColor = MiuixTheme.colorScheme.onSurface
        )
    ) {
        Column {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
            Text(
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.large),
                text = title,
                textAlign = TextAlign.Center,
                style = MiuixTheme.textStyles.title2.copy(fontWeight = FontWeight.Bold),
                color = MiuixTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            Text(
                modifier = Modifier
                    .animateContentSize()
                    .padding(horizontal = MaterialTheme.spacing.large),
                text = description,
                style = MiuixTheme.textStyles.body1,
                color = MiuixTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
private fun OnBoardingCardPreview() {
    BISBIAITheme {
        OnboardingCard(
            title = "Title",
            description = "Description"
        )
    }
}