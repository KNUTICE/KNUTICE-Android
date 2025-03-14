package com.doyoonkim.knutice.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.knutice.model.Destination
import com.doyoonkim.knutice.ui.theme.buttonContainer
import com.doyoonkim.knutice.ui.theme.subTitle
import com.doyoonkim.knutice.R
import com.doyoonkim.knutice.ui.theme.title

// TODO: Apply Color Theme on HorizontalDivider.

@Composable
fun UserPreference(
    modifier: Modifier = Modifier,
    onNotificationPreferenceClicked: (Destination) -> Unit,
    onCustomerServiceClicked: (Destination) -> Unit,
    onOssClicked: (Destination) -> Unit
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
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.title
        )

        HorizontalDivider(
            Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.subTitle
        )

        Column(
            modifier = Modifier.fillMaxWidth().wrapContentSize()
                .padding(top = 15.dp, bottom = 15.dp)
                .clickable {
                    onNotificationPreferenceClicked(Destination.NOTIFICATION)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    text = stringResource(R.string.enable_notification_title),
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.title
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
            text = stringResource(R.string.title_support),
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.title
        )

        HorizontalDivider(
            Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.subTitle
        )

        Row(
            modifier = Modifier.fillMaxWidth().wrapContentSize()
                .padding(top = 5.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.wrapContentHeight().weight(5f),
                text = stringResource(R.string.title_customer_service),
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.title
            )

            IconButton(
                onClick = { onCustomerServiceClicked(Destination.CS) }
            ) {
                Image(
                    painter = painterResource(R.drawable.baseline_arrow_forward_ios_24),
                    contentDescription = "Go to customer service page.",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.buttonContainer)
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
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.title
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
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.title
            )

            Text(
                modifier = Modifier.wrapContentHeight().weight(3f),
                text = stringResource(R.string.version_code),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.title
            )
        }

        HorizontalDivider(
            Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.subTitle
        )

        Row(
            modifier = Modifier.fillMaxWidth().wrapContentSize()
                .padding(top = 5.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.wrapContentHeight().weight(5f),
                text = stringResource(R.string.about_oss),
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.title
            )

            IconButton(
                onClick = { onOssClicked(Destination.OSS) }
            ) {
                Image(
                    painter = painterResource(R.drawable.baseline_arrow_forward_ios_24),
                    contentDescription = "Button to OSS Notice",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.buttonContainer)
                )
            }
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

}