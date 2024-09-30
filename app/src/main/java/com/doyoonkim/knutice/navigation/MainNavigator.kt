package com.doyoonkim.knutice.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.doyoonkim.knutice.model.Destination
import com.doyoonkim.knutice.model.NoticeCategory
import com.doyoonkim.knutice.presentation.CategorizedNotification
import com.doyoonkim.knutice.presentation.MoreCategorizedNotification
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
            MoreCategorizedNotification(category = NoticeCategory.GENERAL_NEWS)
        }

        composable(Destination.MORE_ACADEMIC.name) {
            viewModel.updateState(
                updatedCurrentLocation = Destination.MORE_ACADEMIC
            )
            MoreCategorizedNotification(category = NoticeCategory.ACADEMIC_NEWS)
        }

        composable(Destination.MORE_SCHOLARSHIP.name) {
            viewModel.updateState(
                updatedCurrentLocation = Destination.MORE_SCHOLARSHIP
            )
            MoreCategorizedNotification(category = NoticeCategory.SCHOLARSHIP_NEWS)
        }

        composable(Destination.MORE_EVENT.name) {
            viewModel.updateState(
                updatedCurrentLocation = Destination.MORE_EVENT
            )
            MoreCategorizedNotification(category = NoticeCategory.EVENT_NEWS)
        }
    }
}