package com.doyoonkim.knutice.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.knutice.ui.theme.subTitle
import com.doyoonkim.knutice.ui.theme.title
import com.example.knutice.R

@Composable
fun PermissionRationale(
    modifier: Modifier = Modifier,
    onRequestDecided: (Boolean) -> Unit
) {
    Column(
        modifier = modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.shoutout_extra_large),
            contentDescription = "KNUTICE App Icon",
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .weight(6f)
        )

        Column(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .weight(3f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.permission_rationale_title),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth().wrapContentHeight()

            )

            Text(
                text = stringResource(R.string.permission_rationale_description),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            )
        }

        Row(
           modifier = Modifier.fillMaxWidth().wrapContentHeight()
               .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {  },
                modifier = Modifier.wrapContentHeight().weight(5f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.subTitle,
                    contentColor = MaterialTheme.colorScheme.title,
                    disabledContentColor = Color.Unspecified,
                    disabledContainerColor = Color.Unspecified
                )
            ) {
                Text(
                    text = stringResource(R.string.button_deny),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {  },
                modifier = Modifier.wrapContentHeight().weight(5f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.button_allow),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

    }
}

@Composable
@Preview(showSystemUi = true, locale = "ko-rKR")
fun PermissionRationale_Preview() {
    PermissionRationale(
        Modifier.fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {

    }
}