package com.doyoonkim.knutice.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.knutice.di.components.AppComponent
import com.doyoonkim.knutice.di.components.DaggerCustomerServiceSceneComponent
import com.doyoonkim.knutice.di.components.DaggerNotificationPreferencesSceneComponent
import com.doyoonkim.knutice.di.components.DaggerSettingsSceneComponent
import com.doyoonkim.main.preference.CustomerServiceScreen
import com.doyoonkim.main.preference.NotificationPreferencesScreen
import com.doyoonkim.main.preference.OssNoticeScreen
import com.doyoonkim.main.preference.UserPreferenceScreen
import com.doyoonkim.main.viewmodel.CustomerServiceViewModel
import com.doyoonkim.main.viewmodel.NotificationPreferencesViewModel
import com.doyoonkim.main.viewmodel.SettingsViewModel

fun NavGraphBuilder.preferenceServiceGraph(
    navController: NavController,
    appComponent: AppComponent,
    navToBottomDest: (String) -> Unit
) {
    composable(
        route = NavRoutes.Settings.route,
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
            DaggerSettingsSceneComponent.factory().create(
                systemServices = appComponent,
                networkProvider = appComponent,
                localStorageProvider = appComponent,
                localPreferenceProvider = appComponent
            )
        }

        UserPreferenceScreen(
            modifier = Modifier.padding(horizontal = 10.dp),
            viewModel = viewModel<SettingsViewModel>(factory = sceneComponent.viewModelFactory()),
            onNotificationPreferenceClicked = { navController.navigate(NavRoutes.NotificationPreferences.route) },
            onCustomerServiceClicked = { navController.navigate(NavRoutes.CustomerService.route) },
            onOssClicked = { navController.navigate(NavRoutes.OpenSource.route) },
            onBackPressed = { syncPerformed ->
                if (syncPerformed) {
                    navToBottomDest(GraphRoute.BOOKMARK)
                }
                else navController.popBackStack()
            }
        )
    }

    composable(
        route = NavRoutes.NotificationPreferences.route,
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
            DaggerNotificationPreferencesSceneComponent.factory().create(
                systemServices = appComponent,
                localPreferenceProvider = appComponent,
                networkProvider = appComponent
            )
        }

        NotificationPreferencesScreen(
            modifier = Modifier.padding(horizontal = 10.dp),
            viewModel = viewModel<NotificationPreferencesViewModel>(factory = sceneComponent.viewModelFactory()),
            onBackPressed = { navController.popBackStack() }
        )
    }

    composable(
        route = NavRoutes.CustomerService.route,
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
            DaggerCustomerServiceSceneComponent.factory().create(
                systemService = appComponent,
                localPreferenceProvider = appComponent,
                networkProvider = appComponent
            )
        }

        CustomerServiceScreen(
            modifier = Modifier.padding(horizontal = 10.dp),
            viewModel = viewModel<CustomerServiceViewModel>(factory = sceneComponent.viewModelFactory()),
            onBackPressed = { navController.popBackStack() }
        )
    }

    composable(
        route = NavRoutes.OpenSource.route,
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
        OssNoticeScreen(
            modifier = Modifier.padding(horizontal = 10.dp),
            onBackPressed = { navController.popBackStack() }
        )
    }
}