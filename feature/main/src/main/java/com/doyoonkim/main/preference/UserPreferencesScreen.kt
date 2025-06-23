package com.doyoonkim.main.preference

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.buttonOnBackground
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.ui.CircleGoButton
import com.doyoonkim.common.ui.RoundedCornerColumn
import com.doyoonkim.common.ui.RoundedCornerColumnTextItem
import com.doyoonkim.common.ui.RoundedCornerColumnTextItemWithExtraOnRight
import com.doyoonkim.common.ui.TopAppBarWithBackButton

@Composable
fun UserPreferenceScreen(
    modifier: Modifier = Modifier,
    onNotificationPreferenceClicked: () -> Unit,
    onCustomerServiceClicked: () -> Unit,
    onOssClicked: () -> Unit,
    onBackPressed: () -> Unit,
) {
    BackHandler { onBackPressed() }

    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                titleText = stringResource(R.string.title_preference),
                onBackPressed = onBackPressed
            )
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { innerPadding ->
        Column(
            modifier = modifier.fillMaxWidth()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RoundedCornerColumn(
                backgroundColor = MaterialTheme.colorScheme.secondaryBackground
            ) {
                RoundedCornerColumnTextItemWithExtraOnRight(
                    verticalPadding = 10.dp,
                    titleText = stringResource(R.string.enable_notification_title),
                    subTitleText = null,
                    primaryColor = MaterialTheme.colorScheme.title,
                    secondaryColor = MaterialTheme.colorScheme.subTitle,
                    hasBottomDivider = false
                ) {
                    CircleGoButton(
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.buttonOnBackground,
                        contentColor = MaterialTheme.colorScheme.subTitle,
                        onClick = onNotificationPreferenceClicked
                    )
                }
            }

            RoundedCornerColumn(
                backgroundColor = MaterialTheme.colorScheme.secondaryBackground
            ) {
                RoundedCornerColumnTextItemWithExtraOnRight(
                    verticalPadding = 10.dp,
                    titleText = stringResource(R.string.title_support),
                    subTitleText = null,
                    primaryColor = MaterialTheme.colorScheme.title,
                    secondaryColor = MaterialTheme.colorScheme.subTitle,
                    hasBottomDivider = false
                ) {
                    CircleGoButton(
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.buttonOnBackground,
                        contentColor = MaterialTheme.colorScheme.subTitle,
                        onClick = onCustomerServiceClicked
                    )
                }
            }

            RoundedCornerColumn(
                backgroundColor = MaterialTheme.colorScheme.secondaryBackground
            ) {
                RoundedCornerColumnTextItem(
                    verticalPadding = 12.dp,
                    titleText = stringResource(R.string.about_version),
                    subTitleText = stringResource(R.string.version_code),
                    primaryColor = MaterialTheme.colorScheme.title,
                    secondaryColor = MaterialTheme.colorScheme.subTitle,
                    hasBottomDivider = true
                )

                RoundedCornerColumnTextItemWithExtraOnRight(
                    verticalPadding = 10.dp,
                    titleText = stringResource(R.string.about_oss),
                    subTitleText = null,
                    primaryColor = MaterialTheme.colorScheme.title,
                    secondaryColor = MaterialTheme.colorScheme.subTitle,
                    hasBottomDivider = false
                ) {
                    CircleGoButton(
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.buttonOnBackground,
                        contentColor = MaterialTheme.colorScheme.subTitle,
                        onClick = onOssClicked
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserPreferencesScreen_Preview() {
    UserPreferenceScreen(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        onNotificationPreferenceClicked = {  },
        onCustomerServiceClicked = {  },
        onOssClicked = {  },
        onBackPressed = {  }
    )
}