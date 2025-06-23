package com.doyoonkim.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple

@Composable
fun LabeledToggleSwitch(
    modifier: Modifier = Modifier,
    titleText: String = "Title Text",
    subTitleText: String = "Subtitle Text",
    isChecked: Boolean = false,
    isEnabled: Boolean = false,
    onCheckStatusChanged: (Boolean) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth().wrapContentSize()
            .padding(top = 15.dp, bottom = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.wrapContentHeight().weight(5f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    text = titleText,
                    color = MaterialTheme.colorScheme.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start
                )

                Text(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    text = subTitleText,
                    color = MaterialTheme.colorScheme.subTitle,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start
                )
            }

            Switch(
                checked = isChecked,
                colors = SwitchDefaults.colors().copy(
                    checkedTrackColor = MaterialTheme.colorScheme.variantPurple,
                    checkedThumbColor = Color.White
                ),
                onCheckedChange = {
                    onCheckStatusChanged(it)
                },
                enabled = isEnabled
            )
        }
    }
}