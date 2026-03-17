package com.doyoonkim.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.title

@Composable
fun EntryPointButton(
    modifier: Modifier = Modifier,
    text: String,
    painter: Painter,
    containerColor: Color,
    textColor: Color,
    size: Dp,
    onClicked: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = { onClicked() },
        enabled = true,
        shape = RoundedCornerShape(15.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = textColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(size)
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

@Preview
@Composable
fun EntryPointButton_Preview() {
    EntryPointButton(
        modifier = Modifier
            .fillMaxWidth(),
        text = "학식 조회",
        painter = painterResource(R.drawable.icon_dining_menu),
        containerColor = MaterialTheme.colorScheme.secondaryBackground,
        textColor = MaterialTheme.colorScheme.title,
        size = 140.dp
    ) {  }
}