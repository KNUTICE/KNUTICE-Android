package com.doyoonkim.main.preference

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint.Align
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.buttonPurple
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.ui.LabeledToggleSwitch
import com.doyoonkim.main.viewmodel.NotificationPreferencesViewModel

@Composable
fun NotificationPreferencesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotificationPreferencesViewModel,
    onBackPressed: () -> Unit
) {
    // Context for main status checking
    val context = LocalContext.current
    val uiStatus by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        isMainNotificationPermissionGranted(
            context,
            onResult = { viewModel.updateMainNotificationPermissionStatus(it) }
        )
        viewModel.getTopicSubscriptionStatus()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Once user leave the application for permission settings and getting back.
        isMainNotificationPermissionGranted(
            context,
            onResult = { viewModel.updateMainNotificationPermissionStatus(it) }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
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
                modifier = Modifier.wrapContentHeight()
                    .padding(top = 15.dp, bottom = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
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
                        checked = uiStatus.isMainNotificationPermissionGranted,
                        colors = SwitchDefaults.colors().copy(
                            checkedTrackColor = MaterialTheme.colorScheme.buttonPurple,
                            checkedThumbColor = Color.White
                        ),
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
                isChecked = uiStatus.isEachChannelAllowed[0],
                isEnabled = uiStatus.isMainNotificationPermissionGranted && uiStatus.isSyncCompleted
            ) {
                viewModel.updateChannelPreferenceState(0, it)
            }

            LabeledToggleSwitch(
                modifier = Modifier.padding(start = 10.dp),
                titleText = stringResource(R.string.academic_notification_channel_name),
                subTitleText = stringResource(R.string.academic_notification_channel_description),
                isChecked = uiStatus.isEachChannelAllowed[1],
                isEnabled = uiStatus.isMainNotificationPermissionGranted && uiStatus.isSyncCompleted
            ) {
                viewModel.updateChannelPreferenceState(1, it)
            }

            LabeledToggleSwitch(
                modifier = Modifier.padding(start = 10.dp),
                titleText = stringResource(R.string.scholarship_notification_channel_name),
                subTitleText = stringResource(R.string.scholarship_notification_channel_description),
                isChecked = uiStatus.isEachChannelAllowed[2],
                isEnabled = uiStatus.isMainNotificationPermissionGranted && uiStatus.isSyncCompleted
            ) {
                viewModel.updateChannelPreferenceState(2, it)
            }

            LabeledToggleSwitch(
                modifier = Modifier.padding(start = 10.dp),
                titleText = stringResource(R.string.event_notification_channel_name),
                subTitleText = stringResource(R.string.event_notification_channel_description),
                isChecked = uiStatus.isEachChannelAllowed[3],
                isEnabled = uiStatus.isMainNotificationPermissionGranted && uiStatus.isSyncCompleted
            ) {
                viewModel.updateChannelPreferenceState(3, it)
            }
        }

        if (!uiStatus.isSyncCompleted) {
            Box(
                modifier = Modifier.matchParentSize()
                    .background(Color.Gray.copy(alpha = 0.5f))
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

fun isMainNotificationPermissionGranted(
    context: Context,
    onResult: (Boolean) -> Unit
) {
    val isNotificationAllowed = ContextCompat.checkSelfPermission(
        context, Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

    val isChannelAllowed = (context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        .getNotificationChannel(context.getString(R.string.inapp_notification_channel_id))
        .importance > 0

    if (isNotificationAllowed && isChannelAllowed) onResult(true)
    else onResult(false)
}
