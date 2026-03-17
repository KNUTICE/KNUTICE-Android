package com.doyoonkim.main.preference

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doyoonkim.common.MealResources
import com.doyoonkim.common.NoticeResources
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
import com.doyoonkim.common.ui.TopAppBarWithNavButton
import com.doyoonkim.main.contract.NotificationPrefEvent
import com.doyoonkim.main.contract.NotificationPrefSideEffect
import com.doyoonkim.main.viewmodel.NotificationPreferencesViewModel
import com.doyoonkim.model.MealCategory
import com.doyoonkim.model.NoticeCategory

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
            TopAppBarWithNavButton(
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                imageResource = R.drawable.wifi,
                contentText = stringResource(R.string.error_no_network_connection)
            )
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main System Permission
                item {
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
                }

                // Core Notice Subscription Status
                item {
                    // Current Notices
                    val coreNotices = NoticeCategory.entries.dropLast(1)

                    RoundedCornerColumn(
                        backgroundColor = MaterialTheme.colorScheme.secondaryBackground
                    ) {
                        coreNotices.forEachIndexed { idx, cat ->
                            RoundedCornerColumnTextItemWithExtraOnRight(
                                verticalPadding = 15.dp,
                                titleText = stringResource(
                                    NoticeResources.getStringResourcesByCategory(cat.name)
                                ),
                                subTitleText = stringResource(
                                    NoticeResources.getChannelDescription(cat.name)
                                ),
                                primaryColor = MaterialTheme.colorScheme.title,
                                secondaryColor = MaterialTheme.colorScheme.subTitle,
                                hasBottomDivider = idx < coreNotices.size - 1
                            ) {
                                Switch(
                                    checked = uiStatus.isEachChannelAllowed[idx],
                                    colors = SwitchDefaults.colors().copy(
                                        checkedTrackColor = MaterialTheme.colorScheme.variantPurple,
                                        checkedThumbColor = Color.White
                                    ),
                                    onCheckedChange = {
                                        viewModel.sendUiEvent(
                                            NotificationPrefEvent.UpdateSubscriptionStatus(idx, it)
                                        )
                                    },
                                    enabled = uiStatus.isMainNotificationPermissionGranted && uiStatus.isSyncCompleted
                                )
                            }
                        }
                    }
                }

                // Major Notice Subscription Status
                item {
                    RoundedCornerColumn(
                        backgroundColor = MaterialTheme.colorScheme.secondaryBackground
                    ) {
                        RoundedCornerColumnTextItemWithExtraOnRight(
                            verticalPadding = 15.dp,
                            primaryColor = MaterialTheme.colorScheme.title,
                            secondaryColor = MaterialTheme.colorScheme.subTitle,
                            titleText = stringResource(R.string.title_major_notification_channel_name),
                            subTitleText = stringResource(R.string.description_major_notification_channel),
                            hasBottomDivider = false
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
                }

                // Meal Subscription Status
                item {
                    RoundedCornerColumn(
                        backgroundColor = MaterialTheme.colorScheme.secondaryBackground
                    ) {
                        MealCategory.entries.forEachIndexed { idx, category ->
                            RoundedCornerColumnTextItemWithExtraOnRight(
                                verticalPadding = 15.dp,
                                primaryColor = MaterialTheme.colorScheme.title,
                                secondaryColor = MaterialTheme.colorScheme.subTitle,
                                titleText = MealResources.getLocalizedName(category),
                                subTitleText = MealResources.getLocalizedDescription(category),
                                hasBottomDivider = idx < MealCategory.entries.size - 1
                            ) {
                                Switch(
                                    checked = uiStatus.isMealChannelAllowed[idx],
                                    colors = SwitchDefaults.colors().copy(
                                        checkedTrackColor = MaterialTheme.colorScheme.variantPurple,
                                        checkedThumbColor = Color.White
                                    ),
                                    onCheckedChange = {
                                        viewModel.sendUiEvent(
                                            NotificationPrefEvent.UpdateMealSubscriptionStatus(idx, it)
                                        )
                                    },
                                    enabled = uiStatus.isMainNotificationPermissionGranted && uiStatus.isSyncCompleted
                                )
                            }
                        }
                    }
                }

                // Cosmetic Purpose Spacer
                item {
                    Spacer(Modifier.height(30.dp))
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
