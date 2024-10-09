package com.doyoonkim.knutice.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.doyoonkim.knutice.model.Destination
import com.doyoonkim.knutice.navigation.MainNavigator
import com.doyoonkim.knutice.ui.theme.KNUTICETheme
import com.doyoonkim.knutice.ui.theme.containerBackground
import com.doyoonkim.knutice.ui.theme.notificationType1
import com.doyoonkim.knutice.ui.theme.title
import com.doyoonkim.knutice.viewModel.MainActivityViewModel
import com.example.knutice.R
 import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KNUTICETheme {
                MainServiceScreen()
            }
        }
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
                                    navController.popBackStack(
                                        Destination.MAIN.name,
                                        inclusive = false,
                                        saveState = true
                                    )
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
                            text = stringResource(when(mainAppState.currentLocation) {
                                Destination.MORE_GENERAL -> R.string.general_news
                                Destination.MORE_ACADEMIC -> R.string.academic_news
                                Destination.MORE_SCHOLARSHIP -> R.string.scholarship_news
                                Destination.MORE_EVENT -> R.string.event_news
                                Destination.SETTINGS -> R.string.title_preference
                                else -> R.string.app_name
                            }),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
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
                                navController.navigate(Destination.SETTINGS.name)
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