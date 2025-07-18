package com.doyoonkim.knutice

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.common.theme.KNUTICETheme
import com.doyoonkim.common.ui.PermissionRationaleComposable
import com.doyoonkim.common.R
import com.doyoonkim.common.di.AppPreferences
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.variantPurple
import com.doyoonkim.knutice.di.components.DaggerMainActivityComponent
import com.doyoonkim.main.splash.KnuticeSplashScreen
import com.doyoonkim.main.viewmodel.SplashViewModel
import com.doyoonkim.notification.local.NotificationAlarmScheduler
import kotlinx.coroutines.delay
import javax.inject.Inject

class MainActivity : ComponentActivity() {

     @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
     @Inject lateinit var alarmManager: AlarmManager

    // NavController
    private lateinit var navController: NavHostController
    private val activity = this
    private val receivedIntent = mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerMainActivityComponent.factory().create(application).inject(this)
        super.onCreate(savedInstanceState)
        receivedIntent.value = intent

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            KNUTICETheme {
                val context = LocalContext.current
                navController = rememberNavController()

                var lastProcessedIntent by remember { mutableStateOf<Int?>(receivedIntent.value.hashCode()) }
                var isDeeplinkInProcess by remember { mutableStateOf(false) }

                var isPreProcessCompleted by remember { mutableStateOf(false) }
                if (!isPreProcessCompleted) {
                    KnuticeSplashScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = viewModel<SplashViewModel>(factory = viewModelFactory)
                    ) { result ->
                        if (!result) this.finish()
                        isPreProcessCompleted = true
                    }
                } else {

                    // Permission Status (Alarm and Reminder)
                    var showPermissionRationale by remember { mutableStateOf(false) }
                    // Permission Launcher
                    val requestPermissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestMultiplePermissions()
                    ) { permissions ->
                        permissions.entries.forEach {
                            Log.d("MainServiceScreen", "${it.key}, ${it.value}")
                            if (it.key == Manifest.permission.SCHEDULE_EXACT_ALARM
                                && !alarmManager.canScheduleExactAlarms()) {
                                showPermissionRationale = true
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
                        viewModelFactory = viewModelFactory
                    ) { activity.finish() }

                    if (showPermissionRationale) {
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
                                context.startActivity(settingIntent)
                                showPermissionRationale = false
                            }
                        }
                    }
                }

                if (isDeeplinkInProcess) {
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
                        if (intent.hashCode() != lastProcessedIntent && isPreProcessCompleted) {
                            isDeeplinkInProcess = true
                            lastProcessedIntent = intent.hashCode()

                            intent.data?.let { uri ->
                                navController.navigate(uri.navDestination())
                            }
                            isDeeplinkInProcess = false
                        }
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

    private fun Uri.navDestination(): String {
        return if (this.host != "service") {
            NavRoutes.Home.route
        } else {
            this.encodedPath?.substring(1) ?: NavRoutes.Home.route
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModelStore.clear()
        this.externalCacheDir?.delete()
    }
}