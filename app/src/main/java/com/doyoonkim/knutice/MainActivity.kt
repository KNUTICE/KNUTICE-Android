package com.doyoonkim.knutice

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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.common.theme.KNUTICETheme
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.ui.PermissionRationaleComposable
import com.doyoonkim.common.R
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.notification.local.NotificationAlarmScheduler
import kotlinx.coroutines.delay
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var notificationAlarmScheduler: NotificationAlarmScheduler

    // NavController
    private lateinit var navController: NavHostController
    private val activity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MainApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        val launchedIntent = this.intent

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            KNUTICETheme {
                val context = LocalContext.current
                var showPermissionRationale by remember { mutableStateOf(false) }
                navController = rememberNavController()

                // SharedScaffoldHandling
                var sharedScaffoldState: Triple<Boolean, Boolean, Boolean>
                val backStackEntryState by navController.currentBackStackEntryAsState()
                backStackEntryState?.destination?.route.let {
                    sharedScaffoldState = when(it) {
                        NavRoutes.Home.route -> Triple(true, true, false)
                        NavRoutes.Bookmark.route -> Triple(true, false, true)
                        else -> Triple(false, false, false)
                    }
                }

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
                    delay(200L)     // Reduce workload on MainThread on its first initialization.
                    // Permission check
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.SCHEDULE_EXACT_ALARM
                        )
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (sharedScaffoldState.first) {
                            BottomAppBar(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .background(Color.Transparent)
                                    .clip(RoundedCornerShape(15.dp)),
                                actions = {
                                    // https://developer.android.com/develop/ui/compose/navigation#bottom-nav
                                    BottomNavigationItem(
                                        selected = sharedScaffoldState.second,
                                        enabled = true,
                                        onClick = {
                                            if (!sharedScaffoldState.second) {
                                                navController.navigate(NavRoutes.Home.route)
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(R.drawable.baseline_home_24),
                                                contentDescription = "Main",
                                                modifier = Modifier.padding(bottom = 5.dp)
                                            )
                                        },
                                        label = {
                                            Text(stringResource(R.string.bottom_bar_home))
                                        },
                                        selectedContentColor = MaterialTheme.colorScheme.title,
                                        unselectedContentColor = MaterialTheme.colorScheme.subTitle
                                    )
                                    BottomNavigationItem(
                                        selected = sharedScaffoldState.third,
                                        enabled = true,
                                        onClick = {
                                            if (!sharedScaffoldState.third) {
                                                navController.navigate(NavRoutes.Bookmark.route)
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(R.drawable.baseline_bookmarks_24),
                                                contentDescription = "Bookmarks",
                                                modifier = Modifier.padding(bottom = 5.dp)
                                            )
                                        },
                                        label = {
                                            Text(stringResource(R.string.bottom_bar_bookmark))
                                        },
                                        selectedContentColor = MaterialTheme.colorScheme.title,
                                        unselectedContentColor = MaterialTheme.colorScheme.subTitle
                                    )
                                },
                                containerColor = MaterialTheme.colorScheme.onAnyBackground
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.displayBackground
                ) { contentPadding ->
                    AppNavHost(
                        modifier = Modifier,
                        contentPadding = contentPadding,
                        navController = navController,
                        viewModelFactory = viewModelFactory,
                        onExit = { activity.finish() }
                    )
                    LaunchedEffect(Unit) {
                        // Intent handling (access application via onCreate call; click push notification when app is closed.)
                        Log.d("MainActivity", "Intent received: ${launchedIntent?.data}")
                        navController.handleDeepLink(launchedIntent)
                    }
                }

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
        }
    }

    // Called when intent is being sent while the onCreate() is already called.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        Log.d("MainActivity", "Intent received onNewIntent: ${intent?.data}")

        // Just for extra safety. onNewIntent is called when app receives intent while the onCreate is already being called.
        // Therefore, it is almost guaranteed that navController is initialized.
        if (::navController.isInitialized) {
            navController.handleDeepLink(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModelStore.clear()
        this.externalCacheDir?.delete()
    }
}