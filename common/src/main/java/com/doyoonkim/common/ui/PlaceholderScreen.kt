package com.doyoonkim.common.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.subTitle

@Composable
fun PlaceholderScreen(
    modifier: Modifier = Modifier,
    imageResource: Int,
    imageColor: Color? = null,
    contentText: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(imageResource),
            contentDescription = contentText,
            modifier = Modifier.wrapContentSize()
                .padding(bottom = 20.dp),
            colorFilter = if (imageColor != null) ColorFilter.tint(imageColor) else null
        )

        Text(
            text = contentText,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.subTitle,
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
        )
    }
}

@Composable
fun DashboardPlaceholder(
    modifier: Modifier = Modifier,
    imageResources: Int,
    imageColor: Color,
    contentText: String,
    optionalElements: @Composable () -> Unit = {  }
) {
    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .fillMaxWidth()
                .padding(25.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlaceholderScreen(
                modifier = Modifier.wrapContentHeight()
                    .fillMaxWidth(),
                imageResource = imageResources,
                imageColor = imageColor,
                contentText = contentText
            )
            optionalElements()
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PlaceholderScreen_Preview() {
    PlaceholderScreen(
        modifier = Modifier,
        imageResource = R.drawable.wifi,
        contentText = "There's no bookmark to be listed."
    )
}