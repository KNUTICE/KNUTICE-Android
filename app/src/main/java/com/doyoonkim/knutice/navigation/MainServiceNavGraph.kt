package com.doyoonkim.knutice.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.doyoonkim.common.navigation.Destination
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.common.navigation.NoticeDetail
import com.doyoonkim.common.ui.TipCategory
import com.doyoonkim.knutice.di.components.AppComponent
import com.doyoonkim.knutice.di.components.DaggerCarrelStatusSceneComponent
import com.doyoonkim.knutice.di.components.DaggerHomeSceneComponent
import com.doyoonkim.main.campus.carrel.CarrelStatusScreen
import com.doyoonkim.main.campus.meal.DiningMenuScreen
import com.doyoonkim.main.home.HomeDashboard
import com.doyoonkim.main.tip.TipDetailScreen
import com.doyoonkim.main.viewmodel.CarrelStatusViewModel
import com.doyoonkim.main.viewmodel.HomeViewModel
import com.doyoonkim.model.NoticeCategory

fun NavGraphBuilder.mainServiceNavGraph(
    navController: NavController,
    appComponent: AppComponent,
    onNoticeDetailRequested: (NoticeDetail) -> Unit,
    onTabSwitches: (String) -> Unit,
    onPopBottomNavHistory: () -> Unit = { }
) {
    // ViewModels will be injected via ViewModelFactory
    composable(NavRoutes.Home.route) {
        val sceneComponent = remember(appComponent) {
            DaggerHomeSceneComponent.factory().create(
                systemServices = appComponent,
                networkProvider = appComponent,
                localCacheProvider = appComponent,
                localPreferenceProvider = appComponent,
                firebaseInfrastructureProvider = appComponent
            )
        }

        HomeDashboard(
            modifier = Modifier.padding(horizontal = 5.dp),
            viewModel = viewModel<HomeViewModel>(factory = sceneComponent.viewModelFactory()),
            onSettingsRequested = { navController.navigate(NavRoutes.Settings.route) },
            onGoBackAction = {
                onPopBottomNavHistory()
            },
            onMoreNoticeRequested = { dest ->
                with(NavRoutes.NoticeListDashboard) {
                    // Will be revised in 1.7.2 with updated use case.
                    val route = when (dest) {
                        Destination.MORE_GENERAL -> createRoute(NoticeCategory.GENERAL_NEWS.name)
                        Destination.MORE_ACADEMIC -> createRoute(NoticeCategory.ACADEMIC_NEWS.name)
                        Destination.MORE_SCHOLARSHIP -> createRoute(NoticeCategory.SCHOLARSHIP_NEWS.name)
                        Destination.MORE_EVENT -> createRoute(NoticeCategory.EVENT_NEWS.name)
                        Destination.MORE_EMPLOYMENT -> createRoute(NoticeCategory.EMPLOYMENT_NEWS.name)
                        else -> null
                    }

                    route?.let { dest -> onTabSwitches(dest) }
                }
            },
            onFullContentRequested = { id, url ->
                onNoticeDetailRequested(NoticeDetail(id, url))
            },
            onTipClicked = { category, url ->
                navController.navigate("tipDetail/${category.name}/${Uri.encode(url)}")
            },
            onMoreMajorNoticeRequested = {
                // Tab Switches. TODO: Resolve destination via createRoute function call.
                onTabSwitches(GraphRoute.NOTICE)
            },
            onCarrelStatusRequested = { navController.navigate(NavRoutes.CarrelStatus.route) },
            onDiningMenuRequested = { navController.navigate(NavRoutes.DiningMenu.route) }
        )
    }

    composable(
        route = "tipDetail/{category}/{contentUrl}",
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
        val tipDetail = backStackEntry.arguments?.let {
            Pair(
                it.getString("category") ?: TipCategory.GENERAL_TIP.name,
                Uri.decode(it.getString("contentUrl") ?: "")
            )
        } ?: Pair("", "")

        TipDetailScreen(
            modifier = Modifier.fillMaxSize(),
            tipCategory = tipDetail.first,
            contentUrl = tipDetail.second
        ) {
            navController.popBackStack()
        }
    }

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
        val room = backStackEntry.arguments?.getString("roomId") ?: ""
        val seat = backStackEntry.arguments?.getString("seatNo") ?: ""

        val sceneComponent = remember(appComponent) {
            DaggerCarrelStatusSceneComponent.factory().create(
                systemServices = appComponent,
                networkProvider = appComponent,
                localCacheProvider = appComponent,
                localPreferenceProvider = appComponent
            )
        }

        CarrelStatusScreen(
            modifier = Modifier,
            viewModel = viewModel<CarrelStatusViewModel>(factory = sceneComponent.viewModelFactory()),
            roomId = room
        ) {
            navController.popBackStack()
        }
    }

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
