package com.doyoonkim.knutice.navigation

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.knutice.di.components.AppComponent
import com.doyoonkim.knutice.di.components.DaggerCarrelStatusSceneComponent
import com.doyoonkim.main.campus.carrel.CarrelStatusScreen
import com.doyoonkim.main.campus.meal.DiningMenuScreen
import com.doyoonkim.main.viewmodel.CarrelStatusViewModel

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
        val sceneComponent = remember(appComponent) {
            DaggerCarrelStatusSceneComponent.factory().create(
                appComponent, appComponent, appComponent
            )
        }

        CarrelStatusScreen(
            modifier = Modifier,
            viewModel = viewModel<CarrelStatusViewModel>(factory = sceneComponent.viewModelFactory())
        ) {
            navController.popBackStack()
        }
    }

    // Carrel Status by Deeplink
    // reading-room?roomId=ROOM3&seat=531
    composable(
        route = "reading-room?roomId={roomId}&seat={seatNo}",
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
    ) { backStackEntry ->
        val room = backStackEntry.arguments?.getString("roomId")
        val seat = backStackEntry.arguments?.getString("seatNo")
        Log.d("Navigation", "ROOM:$room SEAT: $seat ")

        val sceneComponent = remember(appComponent) {
            DaggerCarrelStatusSceneComponent.factory().create(
                appComponent, appComponent, appComponent
            )
        }

        CarrelStatusScreen(
            modifier = Modifier,
            viewModel = viewModel<CarrelStatusViewModel>(factory = sceneComponent.viewModelFactory())
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

    // DiningHall by Deeplink
    // meal?topic=STUDENT_CAFETERIA
    composable(
        route = "meal?topic={mealTopic}",
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
    ) { backStackEntry ->
        val selection = backStackEntry.arguments?.getString("mealTopic")

        DiningMenuScreen(
            modifier = Modifier,
            hallSelection = selection ?: ""
        ) {
            navController.popBackStack()
        }
    }

}