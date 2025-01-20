package com.doyoonkim.knutice.presentation

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.doyoonkim.knutice.R
import com.doyoonkim.knutice.model.NoticeCategory
import com.doyoonkim.knutice.ui.theme.subTitle
import com.doyoonkim.knutice.ui.theme.title
import com.doyoonkim.knutice.viewModel.NotificationPreferenceViewModel

@Composable
fun NotificationPreferences(
    modifier: Modifier = Modifier,
    viewModel: NotificationPreferenceViewModel = hiltViewModel(),
    onBackClicked: () -> Unit = {  },
    onMainNotificationSwitchToggled: () -> Unit = {  }
) {
    val context = LocalContext.current
    val status by viewModel.uiStatus.collectAsState()
    var permissionStatus by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.checkMainNotificationPreferenceStatus()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.checkMainNotificationPreferenceStatus()
    }

    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                text = stringResource(R.string.pref_notification_title),
                color = MaterialTheme.colorScheme.title,
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
                            color = MaterialTheme.colorScheme.title,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Start
                        )

                        Text(
                            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                            text = stringResource(R.string.enable_service_notification_sub),
                            color = MaterialTheme.colorScheme.subTitle,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Start
                        )
                    }

                    Switch(
                        checked = status.isMainNotificationPermissionGranted,
                        onCheckedChange = {
                            val settingIntent = Intent(
                                "android.settings.APP_NOTIFICATION_SETTINGS"
                            ).apply {
                                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                this.putExtra(
                                    "android.provider.extra.APP_PACKAGE",
                                    context.packageName
                                )
                            }
                            context.startActivity(settingIntent)
                        },
                        enabled = true
                    )
                }
            }

            LabeledToggleSwitch(
                modifier = Modifier.padding(start = 10.dp),
                titleText = stringResource(R.string.general_notificaiton_channel_name),
                subTitleText = stringResource(R.string.general_notification_channel_description),
                isSwitchChecked = status.isEachChannelAllowed[0],
                isSwitchEnabled = status.isMainNotificationPermissionGranted
            ) {
                viewModel.updateChannelPreference(NoticeCategory.GENERAL_NEWS, it)
            }

            LabeledToggleSwitch(
                modifier = Modifier.padding(start = 10.dp),
                titleText = stringResource(R.string.academic_notification_channel_name),
                subTitleText = stringResource(R.string.academic_notification_channel_description),
                isSwitchChecked = status.isEachChannelAllowed[1],
                isSwitchEnabled = status.isMainNotificationPermissionGranted
            ) {
                viewModel.updateChannelPreference(NoticeCategory.ACADEMIC_NEWS, it)
            }
            LabeledToggleSwitch(
                modifier = Modifier.padding(start = 10.dp),
                titleText = stringResource(R.string.scholarship_notification_channel_name),
                subTitleText = stringResource(R.string.scholarship_notification_channel_description),
                isSwitchChecked = status.isEachChannelAllowed[2],
                isSwitchEnabled = status.isMainNotificationPermissionGranted
            ) {
                viewModel.updateChannelPreference(NoticeCategory.SCHOLARSHIP_NEWS, it)
            }
            LabeledToggleSwitch(
                modifier = Modifier.padding(start = 10.dp),
                titleText = stringResource(R.string.event_notification_channel_name),
                subTitleText = stringResource(R.string.event_notification_channel_description),
                isSwitchChecked = status.isEachChannelAllowed[3],
                isSwitchEnabled = status.isMainNotificationPermissionGranted
            ) {
                viewModel.updateChannelPreference(NoticeCategory.EVENT_NEWS, it)
            }
        }
    }
}

@Composable
fun LabeledToggleSwitch(
    modifier: Modifier = Modifier,
    titleText: String = "Title Text",
    subTitleText: String = "Subtitle Text",
    isSwitchChecked: Boolean = false,
    isSwitchEnabled: Boolean = false,
    onCheckStatusChanged: (Boolean) -> Unit = {  }
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
                checked = isSwitchChecked,
                onCheckedChange = {
                    onCheckStatusChanged(it)
                },
                enabled = isSwitchEnabled
            )
        }
    }
}

@Preview(locale = "ko-rKR")
@Composable
fun NotificationPreferences_Preview() {
    NotificationPreferences()
}