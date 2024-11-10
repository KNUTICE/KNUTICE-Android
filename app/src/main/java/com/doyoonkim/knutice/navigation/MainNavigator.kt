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
import com.doyoonkim.knutice.model.NoticeCategory
import com.doyoonkim.knutice.presentation.CategorizedNotification
import com.doyoonkim.knutice.presentation.DetailedNoticeContent
import com.doyoonkim.knutice.presentation.MoreCategorizedNotification
import com.doyoonkim.knutice.presentation.OpenSourceLicenseNotice
import com.doyoonkim.knutice.presentation.UserPreference
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
        startDestination = Destination.MAIN.name
    ) {
        composable(Destination.MAIN.name) {
            viewModel.updateState(
                updatedCurrentLocation = Destination.MAIN
            )
            CategorizedNotification(navController = navController)
        }

        composable(Destination.MORE_GENERAL.name) {
            viewModel.updateState(
                updatedCurrentLocation = Destination.MORE_GENERAL
            )
            MoreCategorizedNotification(
                category = NoticeCategory.GENERAL_NEWS,
                backButtonHandler = { navController.popBackStack() }
            ) {
                navController.navigate(it)
            }
        }

        composable(Destination.MORE_ACADEMIC.name) {
            viewModel.updateState(
                updatedCurrentLocation = Destination.MORE_ACADEMIC
            )
            MoreCategorizedNotification(
                category = NoticeCategory.ACADEMIC_NEWS,
                backButtonHandler = { navController.popBackStack() }
            ) {
                navController.navigate(it)
            }
        }

        composable(Destination.MORE_SCHOLARSHIP.name) {
            viewModel.updateState(
                updatedCurrentLocation = Destination.MORE_SCHOLARSHIP
            )
            MoreCategorizedNotification(
                category = NoticeCategory.SCHOLARSHIP_NEWS,
                backButtonHandler = { navController.popBackStack() }
            ) {
                navController.navigate(it)
            }
        }

        composable(Destination.MORE_EVENT.name) {
            viewModel.updateState(
                updatedCurrentLocation = Destination.MORE_EVENT
            )
            MoreCategorizedNotification(
                category = NoticeCategory.EVENT_NEWS,
                backButtonHandler = { navController.popBackStack() }
            ) {
                navController.navigate(it)
            }
        }

        composable(Destination.SETTINGS.name) {
            viewModel.updateState(
                updatedCurrentLocation = Destination.SETTINGS
            )
            UserPreference(
                Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp),
                navController
            )
        }

        composable(Destination.OSS.name) {
            viewModel.updateState(
                updatedCurrentLocation = Destination.OSS
            )
            OpenSourceLicenseNotice()
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