package com.doyoonkim.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EntryPointButton(
    modifier: Modifier = Modifier,
    text: String,
    painter: Painter,
    containerColor: Color,
    textColor: Color,
    size: Dp
) {
    Surface(
        modifier = modifier
            .background(Color.Transparent),
        shape = RoundedCornerShape(15.dp),
        color = containerColor
    ) {
        Box(
            modifier = Modifier.size(size)
        ) {
            Text(
                text = text,
                style = TextStyle(
                    color = textColor,
                    fontSize = 18.sp
                ),
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.TopStart)
                    .padding(start = 15.dp, top = 15.dp)
            )

            // Test Images
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .width(140.dp)
                    .align(Alignment.BottomEnd),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}