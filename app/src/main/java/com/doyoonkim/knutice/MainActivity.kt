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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.common.theme.KNUTICETheme
import com.doyoonkim.common.theme.containerBackgroundSolid
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.ui.PermissionRationaleComposable
import com.doyoonkim.common.R
import com.doyoonkim.notification.local.NotificationAlarmScheduler
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val notificationAlarmScheduler by lazy {
        NotificationAlarmScheduler(this)
    }

    // NavController
    private lateinit var navController: NavHostController
    private val activity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MainApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        val launchedIntent = this.intent

//         applicationContext.deleteDatabase("Main Local Database")
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            KNUTICETheme {
                val context = LocalContext.current
                var showPermissionRationale by remember { mutableStateOf(false) }
                navController = rememberNavController()

                // Bottom Bar Handling
                var isBottomBarVisible = true
                val backStackEntryState by navController.currentBackStackEntryAsState()
                backStackEntryState?.destination?.route.let { route ->
                    isBottomBarVisible = when(route) {
                        NavRoutes.Home.route -> true
                        NavRoutes.Bookmark.route -> true
                        else -> false
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
                    // Permission check
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.POST_NOTIFICATIONS,
                            Manifest.permission.SCHEDULE_EXACT_ALARM
                        )
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.containerBackgroundSolid),
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!isBottomBarVisible) {
                                        IconButton(
                                            onClick = {
                                                navController.popBackStack()
                                            }
                                        ) {
                                            Image(
                                                painter = painterResource(R.drawable.baseline_arrow_back_ios_new_24),
                                                contentDescription = "back",
                                                modifier = Modifier.wrapContentSize(),
                                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.title)
                                            )
                                        }
                                    }

                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = stringResource(R.string.app_name),
                                        textAlign = TextAlign.Left,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.title
                                    )
                                }
                            },
                            actions = {
                                if (isBottomBarVisible) {
                                    IconButton(
                                        onClick = {
                                            navController.navigate(NavRoutes.NoticeSearch.route)
                                        }
                                    ) {
                                        Image(
                                            painter = painterResource(R.drawable.baseline_search_24),
                                            contentDescription = "Search",
                                            modifier = Modifier.wrapContentSize(),
                                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.title)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            navController.navigate(NavRoutes.Settings.route)
                                        }
                                    ) {
                                        Image(
                                            painter = painterResource(R.drawable.baseline_settings_24),
                                            contentDescription = "Settings",
                                            modifier = Modifier.wrapContentSize(),
                                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.title)
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.containerBackgroundSolid,
                                titleContentColor = MaterialTheme.colorScheme.title
                            )
                        )
                    },
                    bottomBar = {
                        if (isBottomBarVisible) {
                            BottomAppBar(
                                modifier = Modifier
                                    .background(Color.Transparent),
                                actions = {
                                    // https://developer.android.com/develop/ui/compose/navigation#bottom-nav
                                    BottomNavigationItem(
                                        selected = backStackEntryState?.destination?.route == NavRoutes.Home.route,
                                        enabled = true,
                                        onClick = {
                                            navController.navigate(NavRoutes.Home.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
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
                                        selected = backStackEntryState?.destination?.route == NavRoutes.Bookmark.route,
                                        enabled = true,
                                        onClick = {
                                            navController.navigate(NavRoutes.Bookmark.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
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
                                }
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.containerBackgroundSolid,
                ) { contentPadding ->
                    val paddingValue = contentPadding

                    AppNavHost(
                        modifier = Modifier.padding(contentPadding),
                        navController = navController,
                        viewModelFactory = viewModelFactory
                    )

                    LaunchedEffect(Unit) {
                        // Intent handling (access application via onCreate call; click push notification when app is closed.)
                        Log.d("MainActivity", "Intent received: ${launchedIntent?.data}")
                        navController.handleDeepLink(launchedIntent)
                    }
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