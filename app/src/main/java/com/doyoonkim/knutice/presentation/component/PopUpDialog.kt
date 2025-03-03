package com.doyoonkim.knutice.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PopUpDialog(
    modifier: Modifier = Modifier,
    isVisible: Boolean = false,
    onCloseClicked: () -> Unit = {  },
    dialogContent: @Composable () -> Unit
) {
    AnimatedVisibility(
        modifier = Modifier.wrapContentSize().background(Color.Transparent),
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it + it / 2 }),
        exit = slideOutVertically(targetOffsetY = { it / 2 })
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .clickable(false) { /* DO NOTHING HERE */ }
                .background(Color.Transparent)
                .padding(10.dp)
        ) {
            Column(
                modifier = modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.BottomCenter),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.clickable { onCloseClicked() },
                        text = "Close"
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                        .background(Color.Transparent)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    dialogContent()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PopUpDialog_Preview() {

}

