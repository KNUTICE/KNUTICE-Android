package com.doyoonkim.knutice.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun LazyText(
    modifier: Modifier,
    text: String = "",
    fontSize: TextUnit = 12.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    fontFamily: FontFamily = FontFamily.Default,
    textAlign: TextAlign = TextAlign.Unspecified,
    maxLine: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Visible,
    completion: Boolean = false
) {
    if (!completion) {
        AnimatedGradient(
            modifier = modifier.fillMaxWidth().height(
                with(LocalDensity.current) {
                    fontSize.toDp()
                }
            )
        )
    } else {
        Text(
            modifier = modifier,
            text = text,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            textAlign = textAlign,
            maxLines = maxLine,
            overflow = overflow
        )
    }
}