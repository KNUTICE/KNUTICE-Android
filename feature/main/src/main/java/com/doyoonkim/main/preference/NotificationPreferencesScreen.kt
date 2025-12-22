package com.doyoonkim.main.preference

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.doyoonkim.common.ui.SingleRoundedCornerItem
import com.doyoonkim.common.ui.TopAppBarWithBackButton
import com.doyoonkim.main.contract.NotificationPrefEvent
import com.doyoonkim.main.contract.NotificationPrefSideEffect
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

    BackHandler { viewModel.sendUiEvent(NotificationPrefEvent.GoBack) }

    LaunchedEffect(Unit) {
        // Check Main System Status.
        viewModel.sendUiEvent(NotificationPrefEvent.CheckMainPermissionStatus)
        viewModel.sendUiEvent(NotificationPrefEvent.RequestTopicSubscriptionStatus)

        viewModel.uiSideEffect.collect { effect ->
            when (effect) {
                is NotificationPrefSideEffect.NavToSystemSettings -> {
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
                }
                is NotificationPrefSideEffect.NavToBack -> onBackPressed()
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Once user leave the application for permission settings and getting back.
        viewModel.sendUiEvent(NotificationPrefEvent.CheckMainPermissionStatus)
    }

    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                titleText = stringResource(R.string.title_notification_pref),
                onBackPressed = {
                    viewModel.sendUiEvent(NotificationPrefEvent.GoBack)
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { innerPadding ->
        if (uiStatus.isError) {
            PlaceholderScreen(
                modifier = Modifier.padding(innerPadding),
                imageResource = R.drawable.wifi,
                contentText = stringResource(R.string.error_no_network_connection)
            )
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .verticalScroll(rememberScrollState(0))
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SingleRoundedCornerItem(
                    backgroundColor = MaterialTheme.colorScheme.secondaryBackground,
                    primaryColor = MaterialTheme.colorScheme.title,
                    secondaryColor = MaterialTheme.colorScheme.subTitle,
                    titleText = stringResource(R.string.enable_notification_title),
                    descriptionText = stringResource(R.string.enable_service_notification_sub)
                ) {
                    Switch(
                        checked = uiStatus.isMainNotificationPermissionGranted,
                        colors = SwitchDefaults.colors().copy(
                            checkedTrackColor = MaterialTheme.colorScheme.variantPurple,
                            checkedThumbColor = Color.White
                        ),
                        onCheckedChange = {
                            viewModel.sendUiEvent(NotificationPrefEvent.RequestSystemSettings)
                        },
                        enabled = uiStatus.isSyncCompleted
                    )
                }

                RoundedCornerColumn(
                    backgroundColor = MaterialTheme.colorScheme.secondaryBackground
                ) {
                    RoundedCornerColumnTextItemWithExtraOnRight(
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
                                viewModel.sendUiEvent(
                                    NotificationPrefEvent.UpdateSubscriptionStatus(0, it)
                                )
                            },
                            enabled = uiStatus.isMainNotificationPermissionGranted && uiStatus.isSyncCompleted
                        )
                    }

                    RoundedCornerColumnTextItemWithExtraOnRight(
                        verticalPadding = 15.dp,
                        primaryColor = MaterialTheme.colorScheme.title,
                        secondaryColor = MaterialTheme.colorScheme.subTitle,
                        titleText = stringResource(R.string.academic_notification_channel_name),
                        subTitleText = stringResource(R.string.academic_notification_channel_description)
                    ) {
                        Switch(
                            checked = uiStatus.isEachChannelAllowed[1],
                            colors = SwitchDefaults.colors().copy(
                                checkedTrackColor = MaterialTheme.colorScheme.variantPurple,
                                checkedThumbColor = Color.White
                            ),
                            onCheckedChange = {
                                viewModel.sendUiEvent(
                                    NotificationPrefEvent.UpdateSubscriptionStatus(1, it)
                                )
                            },
                            enabled = uiStatus.isMainNotificationPermissionGranted && uiStatus.isSyncCompleted
                        )
                    }

                    RoundedCornerColumnTextItemWithExtraOnRight(
                        verticalPadding = 15.dp,
                        primaryColor = MaterialTheme.colorScheme.title,
                        secondaryColor = MaterialTheme.colorScheme.subTitle,
                        titleText = stringResource(R.string.scholarship_notification_channel_name),
                        subTitleText = stringResource(R.string.scholarship_notification_channel_description)
                    ) {
                        Switch(
                            checked = uiStatus.isEachChannelAllowed[2],
                            colors = SwitchDefaults.colors().copy(
                                checkedTrackColor = MaterialTheme.colorScheme.variantPurple,
                                checkedThumbColor = Color.White
                            ),
                            onCheckedChange = {
                                viewModel.sendUiEvent(
                                    NotificationPrefEvent.UpdateSubscriptionStatus(2, it)
                                )
                            },
                            enabled = uiStatus.isMainNotificationPermissionGranted && uiStatus.isSyncCompleted
                        )
                    }

                    RoundedCornerColumnTextItemWithExtraOnRight(
                        verticalPadding = 15.dp,
                        primaryColor = MaterialTheme.colorScheme.title,
                        secondaryColor = MaterialTheme.colorScheme.subTitle,
                        titleText = stringResource(R.string.event_notification_channel_name),
                        subTitleText = stringResource(R.string.event_notification_channel_description)
                    ) {
                        Switch(
                            checked = uiStatus.isEachChannelAllowed[3],
                            colors = SwitchDefaults.colors().copy(
                                checkedTrackColor = MaterialTheme.colorScheme.variantPurple,
                                checkedThumbColor = Color.White
                            ),
                            onCheckedChange = {
                                viewModel.sendUiEvent(
                                    NotificationPrefEvent.UpdateSubscriptionStatus(3, it)
                                )
                            },
                            enabled = uiStatus.isMainNotificationPermissionGranted && uiStatus.isSyncCompleted
                        )
                    }

                    RoundedCornerColumnTextItemWithExtraOnRight(
                        verticalPadding = 15.dp,
                        primaryColor = MaterialTheme.colorScheme.title,
                        secondaryColor = MaterialTheme.colorScheme.subTitle,
                        titleText = stringResource(R.string.employment_notification_channel_name),
                        subTitleText = stringResource(R.string.employment_notification_channel_descrption),
                        hasBottomDivider = false
                    ) {
                        Switch(
                            checked = uiStatus.isEachChannelAllowed[4],
                            colors = SwitchDefaults.colors().copy(
                                checkedTrackColor = MaterialTheme.colorScheme.variantPurple,
                                checkedThumbColor = Color.White
                            ),
                            onCheckedChange = {
                                viewModel.sendUiEvent(
                                    NotificationPrefEvent.UpdateSubscriptionStatus(4, it)
                                )
                            },
                            enabled = uiStatus.isMainNotificationPermissionGranted && uiStatus.isSyncCompleted
                        )
                    }
                }


                SingleRoundedCornerItem(
                    modifier = Modifier.padding(top = 10.dp),
                    backgroundColor = MaterialTheme.colorScheme.secondaryBackground,
                    primaryColor = MaterialTheme.colorScheme.title,
                    secondaryColor = MaterialTheme.colorScheme.subTitle,
                    titleText = stringResource(R.string.title_major_notification_channel_name),
                    descriptionText = stringResource(R.string.description_major_notification_channel)
                ) {
                    Switch(
                        checked = uiStatus.isMajorChannelAllowed,
                        colors = SwitchDefaults.colors().copy(
                            checkedTrackColor = MaterialTheme.colorScheme.variantPurple,
                            checkedThumbColor = Color.White
                        ),
                        onCheckedChange = {
                            viewModel.sendUiEvent(
                                NotificationPrefEvent.UpdateMajorSubscriptionStatue(it)
                            )
                        },
                        enabled = uiStatus.isMainNotificationPermissionGranted &&
                                uiStatus.isSyncCompleted &&
                                uiStatus.isMajorSubscribed
                    )
                }
            }

            if (!uiStatus.isSyncCompleted) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                ) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color.Transparent)
                            .clip(RoundedCornerShape(20.dp)),
                        color = MaterialTheme.colorScheme.onAnyBackground
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(25.dp),
                            color = MaterialTheme.colorScheme.variantPurple
                        )
                    }
                }
            }
        }
    }
}
