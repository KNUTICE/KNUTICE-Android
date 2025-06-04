package com.doyoonkim.knutice.navigation

import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.doyoonkim.knutice.model.Destination
import com.doyoonkim.knutice.model.FullContent
import com.doyoonkim.knutice.model.NavDestination
import com.doyoonkim.knutice.model.Notice
import com.doyoonkim.knutice.presentation.BookmarkComposable
import com.doyoonkim.knutice.presentation.CategorizedNotification
import com.doyoonkim.knutice.presentation.CustomerService
import com.doyoonkim.knutice.presentation.DetailedNoticeContent
import com.doyoonkim.knutice.presentation.EditBookmark
import com.doyoonkim.knutice.presentation.MoreCategorizedNotification
import com.doyoonkim.knutice.presentation.NotificationPreferences
import com.doyoonkim.knutice.presentation.OpenSourceLicenseNotice
import com.doyoonkim.knutice.presentation.UserPreference
import com.doyoonkim.knutice.presentation.SearchNotice
import com.doyoonkim.knutice.viewModel.MainServiceViewModel

@Composable
fun MainNavigator(
    modifier: Modifier = Modifier,
    viewModel: MainServiceViewModel = hiltViewModel(),
    navController: NavHostController,
    onExitAction: (Boolean) -> Unit
) {
    // Navigation
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavDestination(arrived = Destination.MAIN),
    ) {
        composable<NavDestination> { backStackEntry ->
            val destination = backStackEntry.toRoute<NavDestination>()
            viewModel.updateState(
                updatedCurrentLocation = destination.arrived
            )

            if (destination.arrived == Destination.CS
                || destination.arrived == Destination.OSS) {
                viewModel.updateState(
                    updatedBottomNavBarVisibility = false
                )
            } else {
                viewModel.updateState(
                    updatedBottomNavBarVisibility = true
                )
            }

            when (destination.arrived) {
                Destination.MAIN -> {
                    CategorizedNotification(
                        onGoBackAction = { navController.popBackStack().also {
                            if (!it) onExitAction(it)
                        } },
                        onMoreNoticeRequested = { navController.navigate(NavDestination(arrived = it)) },
                        onFullContentRequested = {
                            viewModel.updateState(updatedTempReservedNoticeForBookmark = it)
                            navController.navigate(it.toFullContent())
                        }
                    )
                }
                Destination.SETTINGS -> UserPreference(
                    Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp).fillMaxSize(),
                    onCustomerServiceClicked = { navController.navigate(NavDestination(it))},
                    onNotificationPreferenceClicked = { navController.navigate(NavDestination(it)) },
                    onOssClicked = { navController.navigate(NavDestination(it)) },
                    onBackPressed = { navController.popBackStack().also {
                        if (!it) navController.run {
                            navController.navigate(NavDestination(Destination.MAIN))
                        }
                    } }
                )
                Destination.OSS -> OpenSourceLicenseNotice()
                Destination.CS -> { CustomerService(Modifier.padding(15.dp)) }
                Destination.SEARCH -> SearchNotice(
                    onBackClicked = { navController.popBackStack().also {
                        if (!it) navController.run {
                            navController.navigate(NavDestination(Destination.MAIN))
                        }
                    } },
                    onNoticeClicked = {
                        viewModel.updateState(updatedTempReservedNoticeForBookmark = it)
                        navController.navigate(it.toFullContent())
                    }
                )
                Destination.NOTIFICATION -> NotificationPreferences(
                    onBackClicked = { navController.popBackStack() },
                    onMainNotificationSwitchToggled = {  }
                )
                Destination.BOOKMARKS -> BookmarkComposable(
                    modifier =Modifier.fillMaxSize(),
                    onEachItemClicked = { navController.navigate(it) },
                    onBackPressed = { navController.popBackStack().also {
                        if (!it) navController.run {
                            navController.navigate(NavDestination(Destination.MAIN))
                        }
                    } }
                )
                else -> MoreCategorizedNotification(
                    backButtonHandler = { navController.popBackStack().also {
                        if (!it) navController.run {
                            navController.navigate(NavDestination(Destination.MAIN))
                        }
                    } },
                    onNoticeSelected = {
                        viewModel.updateState(updatedTempReservedNoticeForBookmark = it)
                        navController.navigate(it.toFullContent())
                    }
                )
            }
        }

        composable<FullContent> { backStackEntry ->
            val requestedNotice = backStackEntry.toRoute<FullContent>()
            val scaffoldTitle = requestedNotice.title

            viewModel.run {
                if (uiState.value.currentLocation != Destination.DETAILED) {
                    updateState(
                        updatedCurrentLocation = Destination.DETAILED,
                        updatedCurrentScaffoldTitle = scaffoldTitle ?: "Full Content",
                        updatedBottomNavBarVisibility = true
                    )

                    // Need to find the reason for multiple request caused by multiple recomposition
                    if (requestedNotice.nttId != uiState.value.tempReserveNoticeForBookmark.nttId.toString()) {
                        getReservedNotice(requestedNotice.nttId)
                    }
                }
            }
            DetailedNoticeContent(
                onBackPressed = { navController.popBackStack().also {
                    if (!it) navController.run { navController.navigate(NavDestination(Destination.MAIN)) }
                } }
            )
            Spacer(Modifier.height(20.dp))
        }

        // Fetch full content via Deep Link
        composable("deeplink/{nttId}") { backStackEntry ->
            val requested = backStackEntry.arguments?.getString("nttId")

        }

        composable<Notice> {
            viewModel.updateState(
                updatedCurrentLocation = Destination.EDIT_BOOKMARK,
                updatedBottomNavBarVisibility = false
            )
            EditBookmark(
                Modifier.padding(10.dp),
                onDetailedNoticeRequested = {
                    viewModel.updateState(
                        updatedFabVisibility = false,
                    )
                    navController.navigate(it.toFullContent())
                }
            ) { bookmark ->
                Log.d("MainNavigator", "Bookmark instance received: ${bookmark ?: "null"}")
                // onSavedClicked
                if (bookmark != null) {
                    Log.d("MainNavigator", "Bookmark instance received.")
                    viewModel.updateState(
                        updatedCurrentTargetBookmark = bookmark,
                        updatedScheduleTriggered = true
                    )
                }
                navController.navigate(NavDestination(Destination.BOOKMARKS))
            }
        }

    }
}