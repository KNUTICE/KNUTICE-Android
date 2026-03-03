package com.doyoonkim.knutice

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Constraints
import androidx.work.NetworkType
import com.doyoonkim.common.theme.KNUTICETheme
import com.doyoonkim.common.ui.PermissionRationaleComposable
import com.doyoonkim.common.R
import com.doyoonkim.common.analytics.AnalyticsLogger
import com.doyoonkim.common.navigation.DeeplinkHandler
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.knutice.di.components.DaggerMainActivityComponent
import com.doyoonkim.notification.task.PeriodicTokenRegistration
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivity : ComponentActivity() {

     @Inject lateinit var alarmManager: AlarmManager
     @Inject lateinit var analytics: AnalyticsLogger

    // NavController
    private lateinit var navController: NavHostController
    private val activity = this
    private val receivedIntent = mutableStateOf<Intent?>(null)

    private val isDeeplinkInProcess = mutableStateOf<Boolean>(false)
    private val lastProcessedIntent = mutableStateOf<Int?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        val appComponent = (application as MainApplication).appComponent
        DaggerMainActivityComponent.factory().create(appComponent, appComponent)
            .inject(this)

        super.onCreate(savedInstanceState)

        // Periodic Token Registration Event
        // Interval: 30-ish days, Backoff: 12 hours Linear
        val workRequest = PeriodicWorkRequestBuilder<PeriodicTokenRegistration>(
            730, TimeUnit.HOURS
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
        ).setBackoffCriteria(
            BackoffPolicy.LINEAR,
            12,
            TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "Token Registration", ExistingPeriodicWorkPolicy.UPDATE, workRequest
        )

        receivedIntent.value = intent

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            KNUTICETheme {
                val context = LocalContext.current
                navController = rememberNavController()

                // Permission Status (Alarm and Reminder)
                val showPermissionRationale = remember { mutableStateOf(false) }
                // Permission Launcher
                val requestPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    permissions.entries.forEach {
                        Log.d("MainServiceScreen", "${it.key}, ${it.value}")
                        if (it.key == Manifest.permission.SCHEDULE_EXACT_ALARM
                            && !alarmManager.canScheduleExactAlarms()) {
                            showPermissionRationale.value = true
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    delay(200L)     // Reduce workload on MainThread on its first initialization.
                    // Permission check
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.SCHEDULE_EXACT_ALARM
                        )
                    )
                }

                MainServiceScreen(
                    modifier = Modifier,
                    navController = navController,
                ) { activity.finish() }

                if (showPermissionRationale.value) {
                    Dialog(
                        onDismissRequest = { /* DO NOTHING. PERMISSION IS MANDATORY */ }
                    ) {
                        PermissionRationaleComposable(
                            modifier = Modifier,
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
                            context.startActivity(settingIntent).also {
                                showPermissionRationale.value = false
                            }
                        }
                    }
                }
            }

            if (isDeeplinkInProcess.value) {
                Dialog(
                    onDismissRequest = {  }
                ) {
                    Surface(
                        modifier = Modifier.wrapContentSize()
                            .background(Color.Transparent),
                        shape = RoundedCornerShape(15.dp),
                        color = MaterialTheme.colorScheme.onAnyBackground
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(15.dp),
                            color = MaterialTheme.colorScheme.variantPurple,
                            trackColor = MaterialTheme.colorScheme.onAnyBackground
                        )
                    }
                }
            }

            LaunchedEffect(receivedIntent.value) {
                receivedIntent.value?.let { intent ->
                    if (intent.hashCode() != lastProcessedIntent.value) {
                        isDeeplinkInProcess.value = true
                        lastProcessedIntent.value = intent.hashCode()
                        DeeplinkHandler.processDeeplink(intent) { host, destination ->
                            // Analytics
                            analytics.logEvent("CLICK_NOTIFICATION", Bundle().apply {
                                putString("CONTENT_TYPE", host)
                                putString("SOURCE", "PUSH")
                                putString("DESTINATION", destination)
                            })
                            navController.navigate(destination) {
                                launchSingleTop = true
                            }

                        }
                        isDeeplinkInProcess.value = false
                    }
                }
            }
        }
    }

    // Called when intent is being sent while the onCreate() is already called.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("MainActivity", "Intent received onNewIntent: ${intent?.data}")
        receivedIntent.value = intent
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModelStore.clear()
        this.externalCacheDir?.delete()
    }
}