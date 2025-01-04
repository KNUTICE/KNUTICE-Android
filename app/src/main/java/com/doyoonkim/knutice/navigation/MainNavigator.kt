package com.doyoonkim.knutice.navigation

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
import com.doyoonkim.knutice.presentation.CategorizedNotification
import com.doyoonkim.knutice.presentation.CustomerService
import com.doyoonkim.knutice.presentation.DetailedNoticeContent
import com.doyoonkim.knutice.presentation.MoreCategorizedNotification
import com.doyoonkim.knutice.presentation.NotificationPreferences
import com.doyoonkim.knutice.presentation.OpenSourceLicenseNotice
import com.doyoonkim.knutice.presentation.UserPreference
import com.doyoonkim.knutice.presentation.component.SearchNotice
import com.doyoonkim.knutice.viewModel.MainActivityViewModel

@Composable
fun MainNavigator(
    modifier: Modifier = Modifier,
    viewModel: MainActivityViewModel = hiltViewModel(),
    navController: NavHostController
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

            when (destination.arrived) {
                Destination.MAIN -> CategorizedNotification(
                    onGoBackAction = { navController.popBackStack() },
                    onMoreNoticeRequested = { navController.navigate(NavDestination(arrived = it)) },
                    onFullContentRequested = { navController.navigate(it) }
                )
                Destination.SETTINGS -> UserPreference(
                    Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp),
                    onCustomerServiceClicked = { navController.navigate(NavDestination(it))},
                    onNotificationPreferenceClicked = { navController.navigate(NavDestination(it)) }) {
                    navController.navigate(NavDestination(it))
                }
                Destination.OSS -> OpenSourceLicenseNotice()
                Destination.CS -> CustomerService(Modifier.padding(15.dp))
                Destination.SEARCH -> SearchNotice(
                    onBackClicked = { navController.popBackStack() },
                    onNoticeClicked = { navController.navigate(it) }
                )
                Destination.NOTIFICATION -> NotificationPreferences(
                    onBackClicked = { navController.popBackStack() },
                    onMainNotificationSwitchToggled = {  }
                )
                else -> MoreCategorizedNotification(
                    backButtonHandler = { navController.popBackStack() },
                    onNoticeSelected = { navController.navigate(it) }
                )
            }
        }

        composable<FullContent> { backStackEntry ->
            val scaffoldTitle = backStackEntry.toRoute<FullContent>().title
            viewModel.updateState(
                updatedCurrentLocation = Destination.Unspecified,
                updatedCurrentScaffoldTitle = scaffoldTitle ?: "Full Content"
            )
            DetailedNoticeContent()
        }
    }
}