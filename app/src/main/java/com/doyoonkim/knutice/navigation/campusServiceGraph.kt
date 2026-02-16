package com.doyoonkim.knutice.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.knutice.di.components.AppComponent
import com.doyoonkim.main.campus.carrel.CarrelStatusScreen
import com.doyoonkim.main.campus.meal.DiningMenuScreen

fun NavGraphBuilder.campusServiceGraph(
    navController: NavController,
    appComponent: AppComponent,
    contentPadding: PaddingValues
) {

    composable(
        route = NavRoutes.CarrelStatus.route,
        enterTransition = {
            slideIntoContainer(
                animationSpec = tween(300, easing = EaseIn),
                towards = AnimatedContentTransitionScope.SlideDirection.Start
            )
        },
        exitTransition = {
            slideOutOfContainer(
                animationSpec = tween(300, easing = EaseOut),
                towards = AnimatedContentTransitionScope.SlideDirection.End
            )
        }
    ) {
        CarrelStatusScreen(
            modifier = Modifier,
            appPreferences = appComponent.appPreference()   // Access via Provision function
        ) {
            navController.popBackStack()
        }
    }

    composable(
        route = NavRoutes.DiningMenu.route,
        enterTransition = {
            slideIntoContainer(
                animationSpec = tween(300, easing = EaseIn),
                towards = AnimatedContentTransitionScope.SlideDirection.Start
            )
        },
        exitTransition = {
            slideOutOfContainer(
                animationSpec = tween(300, easing = EaseOut),
                towards = AnimatedContentTransitionScope.SlideDirection.End
            )
        }
    ) {
        DiningMenuScreen(
            modifier = Modifier
        ) {
            navController.popBackStack()
        }
    }

}