package com.doyoonkim.main

import android.net.Uri
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.navigation.Destination
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.common.navigation.NoticeDetail
import com.doyoonkim.common.ui.TipCategory
import com.doyoonkim.main.home.HomeScreen
import com.doyoonkim.main.notice.NoticeDetailScreen
import com.doyoonkim.main.notice.NoticeSearchScreen
import com.doyoonkim.main.notice.NoticesInCategoryScreen
import com.doyoonkim.main.preference.CustomerServiceScreen
import com.doyoonkim.main.preference.NotificationPreferencesScreen
import com.doyoonkim.main.preference.OssNoticeScreen
import com.doyoonkim.main.preference.UserPreferenceScreen
import com.doyoonkim.main.splash.KnuticeSplashScreen
import com.doyoonkim.main.tip.TipDetailScreen
import com.doyoonkim.main.viewmodel.CustomerServiceViewModel
import com.doyoonkim.main.viewmodel.HomeViewModel
import com.doyoonkim.main.viewmodel.NoticeDetailViewModel
import com.doyoonkim.main.viewmodel.NoticeSearchViewModel
import com.doyoonkim.main.viewmodel.NoticesInCategoryViewModel
import com.doyoonkim.main.viewmodel.NotificationPreferencesViewModel
import com.doyoonkim.main.viewmodel.SettingsViewModel
import com.doyoonkim.main.viewmodel.SplashViewModel
import com.doyoonkim.model.NoticeCategory

fun NavGraphBuilder.mainServiceNavGraph(
    navController: NavController,
    viewModelFactory: ViewModelProvider.Factory,
    contentPadding: PaddingValues,
    onNoticeDetailRequested: (NoticeDetail) -> Unit,
    onBookmarkServiceRequested: (BookmarkInfo) -> Unit,
    onExit: () -> Unit = {  }
) {

    // ViewModels will be injected via ViewModelFactory
    composable(NavRoutes.Home.route) {
        HomeScreen(
            modifier = Modifier.padding(horizontal = 5.dp),
            viewModel = viewModel<HomeViewModel>(factory = viewModelFactory),
            bottomPadding = contentPadding.calculateBottomPadding(),
            onSearchRequested = { navController.navigate(NavRoutes.NoticeSearch.route) },
            onSettingsRequested = { navController.navigate(NavRoutes.Settings.route) },
            onGoBackAction = {
                navController.popBackStack().also { if (!it) onExit() }
            },
            onMoreNoticeRequested = { dest ->
                navController.run {
                    when(dest) {
                        Destination.MORE_GENERAL -> navigate(NavRoutes.GeneralNotices.route)
                        Destination.MORE_ACADEMIC -> navigate(NavRoutes.AcademicNotices.route)
                        Destination.MORE_SCHOLARSHIP -> navigate(NavRoutes.ScholarshipNotices.route)
                        Destination.MORE_EVENT -> navigate(NavRoutes.EventNotices.route)
                        else -> { /* DO NOTHING. */ }
                    }
                }
            },
            onFullContentRequested = { id, url ->
                onNoticeDetailRequested(NoticeDetail(id, url))
            },
            onTipClicked = { category, url ->
                navController.navigate("tipDetail/${category.name}/${Uri.encode(url)}")
            }
        )
    }

    composable(
        route = NavRoutes.NoticeSearch.route,
        enterTransition = {
            slideIntoContainer(
                animationSpec = tween(300, easing = EaseIn),
                towards = AnimatedContentTransitionScope.SlideDirection.Up
            )
        },
        exitTransition = {
            slideOutOfContainer(
                animationSpec = tween(300, easing = EaseOut),
                towards = AnimatedContentTransitionScope.SlideDirection.Down
            )
        }
    ) {
        NoticeSearchScreen(
            modifier = Modifier,
            viewModel = viewModel<NoticeSearchViewModel>(factory = viewModelFactory),
            onBackPressed = { navController.popBackStack() },
            onNoticeSelected = { id, url ->
                onNoticeDetailRequested(NoticeDetail(id, url))
            }
        )
    }

    composable(
        route = NavRoutes.GeneralNotices.route,
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
        NoticesInCategoryScreen(
            modifier = Modifier,
            category = NoticeCategory.GENERAL_NEWS,
            viewModel = viewModel<NoticesInCategoryViewModel>(factory = viewModelFactory),
            onBackButtonPressed = { navController.popBackStack() },
            onNoticeSelected = { id, url ->
                onNoticeDetailRequested(NoticeDetail(id, url))
            }
        )
    }

    composable(
        route = NavRoutes.AcademicNotices.route,
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
        NoticesInCategoryScreen(
            modifier = Modifier,
            category = NoticeCategory.ACADEMIC_NEWS,
            viewModel = viewModel<NoticesInCategoryViewModel>(factory = viewModelFactory),
            onBackButtonPressed = { navController.popBackStack() },
            onNoticeSelected = { id, url ->
                onNoticeDetailRequested(NoticeDetail(id, url))
            }
        )
    }

    composable(
        route = NavRoutes.ScholarshipNotices.route,
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
        NoticesInCategoryScreen(
            modifier = Modifier,
            category = NoticeCategory.SCHOLARSHIP_NEWS,
            viewModel = viewModel<NoticesInCategoryViewModel>(factory = viewModelFactory),
            onBackButtonPressed = { navController.popBackStack() },
            onNoticeSelected = { id, url ->
                onNoticeDetailRequested(NoticeDetail(id, url))
            }
        )
    }

    composable(
        route = NavRoutes.EventNotices.route,
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
        NoticesInCategoryScreen(
            modifier = Modifier,
            category = NoticeCategory.EVENT_NEWS,
            viewModel = viewModel<NoticesInCategoryViewModel>(factory = viewModelFactory),
            onBackButtonPressed = { navController.popBackStack() },
            onNoticeSelected = { id, url ->
                onNoticeDetailRequested(NoticeDetail(id, url))
            }
        )
    }

    // preferences
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
        UserPreferenceScreen(
            modifier = Modifier.padding(horizontal = 10.dp),
            viewModel = viewModel<SettingsViewModel>(factory = viewModelFactory),
            onNotificationPreferenceClicked = { navController.navigate(NavRoutes.NotificationPreferences.route) },
            onCustomerServiceClicked = { navController.navigate(NavRoutes.CustomerService.route) },
            onOssClicked = { navController.navigate(NavRoutes.OpenSource.route) },
            onBackPressed = { syncPerformed ->
                if (syncPerformed) {
                    navController.popBackStack(NavRoutes.Home.route, inclusive = true)
                    navController.navigate(NavRoutes.Bookmark.route)
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
        NotificationPreferencesScreen(
            modifier = Modifier.padding(horizontal = 10.dp),
            viewModel = viewModel<NotificationPreferencesViewModel>(factory = viewModelFactory),
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
        CustomerServiceScreen(
            modifier = Modifier.padding(horizontal = 10.dp),
            viewModel = viewModel<CustomerServiceViewModel>(factory = viewModelFactory),
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

    // DeepLinks for NoticeDetailScreen
    composable(
        route = "noticeDetail/{nttId}/{contentUrl}/{isFabVisible}",
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "knutice://service/noticeDetail/{nttId}/{contentUrl}/{isFabVisible}"
            }
        ),
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
        val noticeInfo = backStackEntry.arguments?.let {
            Triple(
                it.getString("nttId")?.toInt() ?: 0,
                Uri.decode(it.getString("contentUrl") ?: ""),
                it.getString("isFabVisible").toBoolean() ?: false
            )
        } ?: Triple(0, "", false)

        NoticeDetailScreen(
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel<NoticeDetailViewModel>(factory = viewModelFactory),
            noticeInfo = noticeInfo,
            onBookmarkCreate = { onBookmarkServiceRequested(BookmarkInfo(
                noticeId = it.nttId,
                noticeTitle = it.title,
                noticeInfo = it.noticeName
            )) },
            onBackPressed = { navController.popBackStack() }
        )
    }

    composable(
        route = "tipDetail/{category}/{contentUrl}",
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "knutice://service/tipDetail/{category}/{contentUrl}"
            }
        ),
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
}