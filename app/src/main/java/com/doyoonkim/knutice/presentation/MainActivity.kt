package com.doyoonkim.knutice.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.doyoonkim.knutice.model.Destination
import com.doyoonkim.knutice.navigation.MainNavigator
import com.doyoonkim.knutice.ui.theme.KNUTICETheme
import com.doyoonkim.knutice.ui.theme.containerBackground
import com.doyoonkim.knutice.ui.theme.notificationType1
import com.doyoonkim.knutice.ui.theme.title
import com.doyoonkim.knutice.viewModel.MainActivityViewModel
import com.doyoonkim.knutice.R
import com.doyoonkim.knutice.model.NavDestination
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is already granted, and Push Notification is available
        } else {
            // Need to inform user that the app won't display push notification.
            // TODO: Add small pop-up style composable to be shown to indicate that a push notification won't available.
        }
    }

    @Composable
    private fun AskNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager
                .PERMISSION_GRANTED
            ) {
                // Permission is already granted, and Push Notification is available
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // RequestPermissionRationale does not triggered.
//                Log.d("MainActivity", "Triggered")
//                PermissionRationale(Modifier.fillMaxWidth()) { result ->
//                    if (result) requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//                }
            }
            else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            KNUTICETheme {
                // Permission Check
                AskNotificationPermission()
                MainServiceScreen()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModelStore.clear()
        this.externalCacheDir?.delete()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainServiceScreen(
    viewModel: MainActivityViewModel = hiltViewModel()
) {
    val mainAppState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.wrapContentSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        if (mainAppState.currentLocation != Destination.MAIN) {
                            IconButton(
                                onClick = {
                                    navController.popBackStack()
                                }
                            ) {
                                Image(
                                    painter = if (mainAppState.currentLocation == Destination.CS) {
                                        painterResource(R.drawable.baseline_close_24)
                                    } else {
                                        painterResource(R.drawable.baseline_arrow_back_ios_new_24)
                                    },
                                    contentDescription = "back",
                                    modifier = Modifier.wrapContentSize(),
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.title)
                                )
                            }
                        }
                        Text(
                            text = when (mainAppState.currentLocation) {
                                Destination.MAIN -> stringResource(R.string.app_name)
                                Destination.MORE_GENERAL -> stringResource(R.string.general_news)
                                Destination.MORE_ACADEMIC -> stringResource(R.string.academic_news)
                                Destination.MORE_SCHOLARSHIP -> stringResource(R.string.scholarship_news)
                                Destination.MORE_EVENT -> stringResource(R.string.event_news)
                                Destination.SETTINGS -> stringResource(R.string.title_preference)
                                Destination.OSS -> stringResource(R.string.oss_notice)
                                Destination.CS -> stringResource(R.string.title_customer_service)
                                Destination.Unspecified -> mainAppState.currentScaffoldTitle
                            },
                            textAlign = if (mainAppState.currentLocation == Destination.CS) {
                                TextAlign.Center
                            } else {
                                TextAlign.Start
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.title,
                    containerColor = MaterialTheme.colorScheme.containerBackground
                ),
                actions = {
                    if (mainAppState.currentLocation == Destination.MAIN) {
                        IconButton(
                            onClick = {
                                navController.navigate(NavDestination(Destination.SETTINGS))
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
                }
            )
        }
    ) { innerPadding ->
        MainNavigator(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier,
        color = MaterialTheme.colorScheme.notificationType1
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    KNUTICETheme {
        MainServiceScreen()
    }
}