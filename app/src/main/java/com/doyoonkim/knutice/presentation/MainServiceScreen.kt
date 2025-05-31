package com.doyoonkim.knutice.presentation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.doyoonkim.knutice.R
import com.doyoonkim.knutice.model.Bookmark
import com.doyoonkim.knutice.model.Destination
import com.doyoonkim.knutice.model.NavDestination
import com.doyoonkim.knutice.navigation.MainNavigator
import com.doyoonkim.knutice.ui.theme.bottomNavContainer
import com.doyoonkim.knutice.ui.theme.containerBackground
import com.doyoonkim.knutice.ui.theme.containerBackgroundSolid
import com.doyoonkim.knutice.ui.theme.displayBackground
import com.doyoonkim.knutice.ui.theme.subTitle
import com.doyoonkim.knutice.ui.theme.title
import com.doyoonkim.knutice.viewModel.MainServiceViewModel
import kotlinx.coroutines.async

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainServiceScreen(
    viewModel: MainServiceViewModel = hiltViewModel(),
    onScheduleAlarmTriggered: (Bookmark) -> Unit,               // Should be refactored later
) {
    val mainAppState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()

//    //TODO Live translation feature (TBD)
//    var showLanguageDownloadRationale by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) {
//        if (Locale.getDefault() != Locale.KOREAN) {
//            showLanguageDownloadRationale = true
//        }
//    }

    LaunchedEffect(mainAppState.scheduleTriggered) {
        Log.d("MainServiceScreen", "Triggered")
        if (!mainAppState.scheduleTriggered || mainAppState.currentTargetBookmark.bookmarkId == -1) {
            return@LaunchedEffect
        }
        val scheduleResult = async {
            kotlin.runCatching {
                onScheduleAlarmTriggered(mainAppState.currentTargetBookmark)
            }
        }
        scheduleResult.await().fold(
            onSuccess = {
                viewModel.updateState(
                    updatedCurrentTargetBookmark = Bookmark(-1),
                    updatedScheduleTriggered = false
                )
            },
            onFailure = {
                Log.d("MainServiceScreen", "Unable to schedule alarm.\nREASON:${it}")
                viewModel.updateState(
                    updatedScheduleTriggered = false
                )
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.containerBackgroundSolid),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.wrapContentSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (mainAppState.currentLocation != Destination.MAIN
                            && mainAppState.currentLocation != Destination.BOOKMARKS
                        ) {
                            IconButton(
                                onClick = {
                                    navController.popBackStack().also {
                                        viewModel.updateState(updatedFabVisibility = true)
                                    }
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
                                Destination.SEARCH -> stringResource(R.string.title_search)
                                Destination.NOTIFICATION -> stringResource(R.string.title_notification_pref)
                                Destination.BOOKMARKS -> stringResource(R.string.app_name)
                                Destination.EDIT_BOOKMARK -> stringResource(R.string.title_edit_bookmark)
                                Destination.DETAILED -> mainAppState.currentScaffoldTitle
                                Destination.Unspecified -> mainAppState.currentLocation.name
                            },
                            textAlign = if (mainAppState.currentLocation == Destination.CS ||
                                mainAppState.currentLocation == Destination.SEARCH
                            ) {
                                TextAlign.Center
                            } else {
                                TextAlign.Start
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.title
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.title,
                    containerColor = MaterialTheme.colorScheme.containerBackgroundSolid
                ),
                actions = {
                    if (mainAppState.currentLocation == Destination.MAIN) {
                        IconButton(
                            onClick = {
                                navController.navigate(NavDestination(Destination.SEARCH))
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
        },
        floatingActionButton = {
            if (mainAppState.currentLocation == Destination.DETAILED && mainAppState.isFabVisible) {
                FloatingActionButton(
                    onClick = {
                        if (mainAppState.tempReserveNoticeForBookmark.title.isNotBlank()) {
                            navController.navigate(mainAppState.tempReserveNoticeForBookmark)
                        }
                    }
                ) {
                    Icon(Icons.Filled.Add, "Floating Action Button")
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = mainAppState.currentLocation == Destination.MAIN
                        || mainAppState.currentLocation == Destination.BOOKMARKS,
                enter = slideInVertically(initialOffsetY = { it + (it * 1 / 2) }),
                exit = slideOutVertically(targetOffsetY = { it + (it * 1 / 2) })
            ) {
                BottomAppBar(
                    modifier = Modifier
                        .background(Color.Transparent),
//                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
//                    .offset(y = 20.dp)
                    actions = {
                        BottomNavigationItem(
                            selected = mainAppState.currentLocation == Destination.MAIN,
                            enabled = true,
                            onClick = {
                                navController.navigate(NavDestination(Destination.MAIN))
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
                            selected = mainAppState.currentLocation == Destination.BOOKMARKS,
                            enabled = true,
                            onClick = {
                                navController.navigate(NavDestination(Destination.BOOKMARKS))
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
                    containerColor = MaterialTheme.colorScheme.bottomNavContainer,
                    contentColor = MaterialTheme.colorScheme.title
                )
            }
            if (mainAppState.currentLocation != Destination.EDIT_BOOKMARK) {

            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        val adjustmentFactor = 10.dp
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

        //TODO Live Translation Feature (TBD)
//        AnimatedVisibility(
//            visible = showLanguageDownloadRationale,
//            enter = scaleIn(),
//            exit = scaleOut()
//        ) {
//            Box(
//                modifier = Modifier.fillMaxSize()
//                    .clickable { showLanguageDownloadRationale = false }
//            ) {
//                PermissionRationaleComposable(
//                    modifier = Modifier.align(Alignment.Center).padding(start = 20.dp, end = 20.dp),
//                    permissionName = stringResource(R.string.text_language),
//                    rationaleTitle = stringResource(R.string.title_langauge_model_download),
//                    description = stringResource(R.string.description_language_model_download)
//                ) {
//                    viewModel.requestModelDownload()
//                    showLanguageDownloadRationale = true
//                }
//            }
//        }
    }
}