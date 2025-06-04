package com.doyoonkim.knutice.presentation

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.doyoonkim.knutice.R
import com.doyoonkim.knutice.alarm.NotificationAlarmScheduler
import com.doyoonkim.knutice.model.FullContent
import com.doyoonkim.knutice.navigation.MainNavigator
import com.doyoonkim.knutice.presentation.component.PermissionRationaleComposable
import com.doyoonkim.knutice.ui.theme.KNUTICETheme
import com.doyoonkim.knutice.ui.theme.containerBackgroundSolid
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationAlarmScheduler by lazy {
        NotificationAlarmScheduler(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//         applicationContext.deleteDatabase("Main Local Database")
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            KNUTICETheme {
                val context = LocalContext.current
                val navController = rememberNavController()
                var showPermissionRationale by remember { mutableStateOf(false) }

                // Permission Launcher
                val requestPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    permissions.entries.forEach {
                        Log.d("MainServiceScreen", "${it.key}, ${it.value}")
                        if (it.key == Manifest.permission.SCHEDULE_EXACT_ALARM
                            && !notificationAlarmScheduler.canScheduleExactAlarms()) {
                            showPermissionRationale = true
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    // Permission check
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.SCHEDULE_EXACT_ALARM
                        )
                    )

                    // Handling Push Notification Click Action
                    // Check there's a extra or not.
                    val requestedIntent = this@MainActivity.intent
                    if (requestedIntent.getStringExtra("nttId") != null) {
                        // Navigate to Detailed Notice

                        // When the app is in background or killed, Data Payload would be delivered once the user
                        // clicks the system tray. (Data Payload will be delivered as Intent)
                        FullContent(
                            requestedIntent.getStringExtra("contentTitle"),
                            requestedIntent.getStringExtra("noticeName"),
                            requestedIntent.getStringExtra("contentUrl") ?: "",
                            requestedIntent.getStringExtra("contentImage") ?: "",
                            requestedIntent.getStringExtra("nttId").toString()
                        ).run {
                            navController.navigate(this)
                        }
                    }
                }

                MainServiceScreen(
                    navController = navController,
                    onScheduleAlarmTriggered = {
                            alarmTarget ->
                        if (!alarmTarget.isScheduled) notificationAlarmScheduler.cancel(alarmTarget)
                        else notificationAlarmScheduler.schedule(alarmTarget)
                    }
                ) { innerPadding ->
                    MainNavigator(
                        navController = navController, modifier = Modifier
                            .consumeWindowInsets(WindowInsets.systemBars)
                            .padding(
                                PaddingValues(
                                    top = innerPadding.calculateTopPadding(),
                                    bottom = innerPadding.calculateBottomPadding()
                                )
                            )
                            .background(MaterialTheme.colorScheme.containerBackgroundSolid)
                    )
                }

                AnimatedVisibility(
                    visible = showPermissionRationale,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .clickable { showPermissionRationale = false }
                    ) {
                        PermissionRationaleComposable(
                            modifier = Modifier.align(Alignment.Center).padding(start = 20.dp, end = 20.dp),
                            permissionName = stringResource(R.string.title_alarm_and_reminder),
                            rationaleTitle = stringResource(R.string.text_rationale_title),
                            description = stringResource(R.string.text_rationale_description)
                        ) {
                            val settingIntent = Intent(
                                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                            ).apply {
                                this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                this.putExtra(
                                    "android.provider.extra.APP_PACKAGE",
                                    context.packageName
                                )
                            }
                            context.startActivity(settingIntent)
                            showPermissionRationale = false
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModelStore.clear()
        this.externalCacheDir?.delete()
    }
}