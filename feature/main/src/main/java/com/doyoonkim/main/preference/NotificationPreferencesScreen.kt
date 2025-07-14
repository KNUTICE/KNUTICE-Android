package com.doyoonkim.main.preference

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.common.ui.PlaceholderScreen
import com.doyoonkim.common.ui.RoundedCornerColumn
import com.doyoonkim.common.ui.RoundedCornerColumnTextItemWithExtraOnRight
import com.doyoonkim.common.ui.TopAppBarWithBackButton
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

    BackHandler {
        if (uiStatus.isSyncCompleted) onBackPressed()
    }

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

    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                titleText = stringResource(R.string.title_notification_pref),
                onBackPressed = onBackPressed
            )
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { innerPadding ->
        if (uiStatus.isError) {
            PlaceholderScreen(
                modifier = Modifier.padding(innerPadding),
                imageResource = R.drawable.outline_network_check_24,
                contentText = stringResource(R.string.error_no_network_connection)
            )
        } else {
            RoundedCornerColumn(
                modifier = modifier.fillMaxWidth().padding(innerPadding),
                backgroundColor = MaterialTheme.colorScheme.secondaryBackground
            ) {
                RoundedCornerColumnTextItemWithExtraOnRight(
                    verticalPadding = 15.dp,
                    titleText = stringResource(R.string.enable_notification_title),
                    subTitleText = stringResource(R.string.enable_service_notification_sub),
                    primaryColor = MaterialTheme.colorScheme.title,
                    secondaryColor = MaterialTheme.colorScheme.subTitle,
                    hasBottomDivider = true
                ) {
                    Switch(
                        checked = uiStatus.isMainNotificationPermissionGranted,
                        colors = SwitchDefaults.colors().copy(
                            checkedTrackColor = MaterialTheme.colorScheme.variantPurple,
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
                        enabled = uiStatus.isSyncCompleted
                    )
                }

                RoundedCornerColumnTextItemWithExtraOnRight(
                    modifier = Modifier.padding(start = 10.dp),
                    verticalPadding = 15.dp,
                    titleText = stringResource(R.string.general_notificaiton_channel_name),
                    subTitleText = stringResource(R.string.general_notification_channel_description),
                    primaryColor = MaterialTheme.colorScheme.title,
                    secondaryColor = MaterialTheme.colorScheme.subTitle,
                    hasBottomDivider = true
                ) {
                    Switch(
                        checked = uiStatus.isEachChannelAllowed[0],
                        colors = SwitchDefaults.colors().copy(
                            checkedTrackColor = MaterialTheme.colorScheme.variantPurple,
                            checkedThumbColor = Color.White
                        ),
                        onCheckedChange = {
                            viewModel.updateChannelPreferenceState(0, it)
                        },
                        enabled = uiStatus.isMainNotificationPermissionGranted && uiStatus.isSyncCompleted
                    )
                }

                RoundedCornerColumnTextItemWithExtraOnRight(
                    modifier = Modifier.padding(start = 10.dp),
                    verticalPadding = 15.dp,
                    titleText = stringResource(R.string.academic_notification_channel_name),
                    subTitleText = stringResource(R.string.academic_notification_channel_description),
                    primaryColor = MaterialTheme.colorScheme.title,
                    secondaryColor = MaterialTheme.colorScheme.subTitle,
                    hasBottomDivider = true
                ) {
                    Switch(
                        checked = uiStatus.isEachChannelAllowed[1],
                        colors = SwitchDefaults.colors().copy(
                            checkedTrackColor = MaterialTheme.colorScheme.variantPurple,
                            checkedThumbColor = Color.White
                        ),
                        onCheckedChange = {
                            viewModel.updateChannelPreferenceState(1, it)
                        },
                        enabled = uiStatus.isMainNotificationPermissionGranted && uiStatus.isSyncCompleted
                    )
                }

                RoundedCornerColumnTextItemWithExtraOnRight(
                    modifier = Modifier.padding(start = 10.dp),
                    verticalPadding = 15.dp,
                    titleText = stringResource(R.string.scholarship_notification_channel_name),
                    subTitleText = stringResource(R.string.scholarship_notification_channel_description),
                    primaryColor = MaterialTheme.colorScheme.title,
                    secondaryColor = MaterialTheme.colorScheme.subTitle,
                    hasBottomDivider = true
                ) {
                    Switch(
                        checked = uiStatus.isEachChannelAllowed[2],
                        colors = SwitchDefaults.colors().copy(
                            checkedTrackColor = MaterialTheme.colorScheme.variantPurple,
                            checkedThumbColor = Color.White
                        ),
                        onCheckedChange = {
                            viewModel.updateChannelPreferenceState(2, it)
                        },
                        enabled = uiStatus.isMainNotificationPermissionGranted && uiStatus.isSyncCompleted
                    )
                }

                RoundedCornerColumnTextItemWithExtraOnRight(
                    modifier = Modifier.padding(start = 10.dp),
                    verticalPadding = 15.dp,
                    titleText = stringResource(R.string.event_notification_channel_name),
                    subTitleText = stringResource(R.string.event_notification_channel_description),
                    primaryColor = MaterialTheme.colorScheme.title,
                    secondaryColor = MaterialTheme.colorScheme.subTitle,
                    hasBottomDivider = false
                ) {
                    Switch(
                        checked = uiStatus.isEachChannelAllowed[3],
                        colors = SwitchDefaults.colors().copy(
                            checkedTrackColor = MaterialTheme.colorScheme.variantPurple,
                            checkedThumbColor = Color.White
                        ),
                        onCheckedChange = {
                            viewModel.updateChannelPreferenceState(3, it)
                        },
                        enabled = uiStatus.isMainNotificationPermissionGranted && uiStatus.isSyncCompleted
                    )
                }
            }
            if (!uiStatus.isSyncCompleted) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(Color.Transparent)
                ) {
                    Surface(
                        modifier = Modifier.align(Alignment.Center)
                            .background(Color.Transparent)
                            .clip(RoundedCornerShape(20.dp)),
                        color = MaterialTheme.colorScheme.onAnyBackground
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                                .padding(25.dp),
                            color = MaterialTheme.colorScheme.variantPurple
                        )
                    }
                }
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
