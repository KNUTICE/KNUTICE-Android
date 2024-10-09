package com.doyoonkim.knutice.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.knutice.ui.theme.subTitle
import com.example.knutice.R

// TODO: Apply Color Theme on HorizontalDivider.

@Composable
fun UserPreference(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            text = stringResource(R.string.pref_notification_title),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )

        HorizontalDivider(
            Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.subTitle
        )

        Column(
            modifier = Modifier.fillMaxWidth().wrapContentSize()
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
                        text = stringResource(R.string.enable_notification_title),
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Start
                    )

                    Text(
                        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                        text = stringResource(R.string.enable_service_notification_sub),
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start
                    )
                }

                Switch(
                    checked = false,
                    onCheckedChange = {  },
                    enabled = false
                )
            }
        }

        HorizontalDivider(
            Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.subTitle
        )

        Text(
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                .padding(top = 20.dp),
            text = stringResource(R.string.about_title),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )

        HorizontalDivider(
            Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.subTitle
        )

        Row(
            modifier = Modifier.fillMaxWidth().wrapContentSize()
                .padding(top = 15.dp, bottom = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.wrapContentHeight().weight(3f),
                text = stringResource(R.string.about_version),
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                textAlign = TextAlign.Start
            )

            Text(
                modifier = Modifier.wrapContentHeight().weight(3f),
                text = stringResource(R.string.version_code),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                textAlign = TextAlign.End
            )
        }

        HorizontalDivider(
            Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.subTitle
        )

        Row(
            modifier = Modifier.fillMaxWidth().wrapContentSize()
                .padding(top = 15.dp, bottom = 15.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                modifier = Modifier.wrapContentHeight().weight(5f),
                text = stringResource(R.string.about_oss),
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                textAlign = TextAlign.Start
            )

            //TODO: Should be replaced with Actual Icon Button.
            Text(
                modifier = Modifier.wrapContentHeight().weight(2f),
                text = ">",
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                textAlign = TextAlign.End
            )
        }

        HorizontalDivider(
            Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.subTitle
        )

    }
}


@Preview(showSystemUi = true, locale = "KO")
@Composable
fun UserPreference_Preview() {
    UserPreference(Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp))
}