package com.doyoonkim.knutice.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.knutice.di.components.AppComponent
import com.doyoonkim.main.campus.carrel.CarrelStatusScreen

fun NavGraphBuilder.campusServiceGraph(
    navController: NavController,
    appComponent: AppComponent,
    contentPadding: PaddingValues
) {

    composable(NavRoutes.CarrelStatus.route) {
        CarrelStatusScreen(
            modifier = Modifier,
            appPreferences = appComponent.appPreference()   // Access via Provision function
        ) {
            navController.popBackStack()
        }
    }


}