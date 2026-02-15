package com.doyoonkim.knutice.navigation

import android.net.Uri
import android.os.Bundle
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.navigation.Destination
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.common.navigation.NoticeDetail
import com.doyoonkim.common.ui.TipCategory
import com.doyoonkim.knutice.di.components.AppComponent
import com.doyoonkim.knutice.di.components.DaggerCustomerServiceSceneComponent
import com.doyoonkim.knutice.di.components.DaggerHomeSceneComponent
import com.doyoonkim.knutice.di.components.DaggerNoticeByMajorSceneComponent
import com.doyoonkim.knutice.di.components.DaggerNoticeDetailSceneComponent
import com.doyoonkim.knutice.di.components.DaggerNoticeInCategorySceneComponent
import com.doyoonkim.knutice.di.components.DaggerNoticeSearchSceneComponent
import com.doyoonkim.knutice.di.components.DaggerNotificationPreferencesSceneComponent
import com.doyoonkim.knutice.di.components.DaggerSettingsSceneComponent
import com.doyoonkim.knutice.di.util.DefaultSystemService
import com.doyoonkim.main.home.HomeDashboard
import com.doyoonkim.main.notice.NoticeByMajorScreen
import com.doyoonkim.main.notice.NoticeDetailScreen
import com.doyoonkim.main.notice.NoticeSearchScreen
import com.doyoonkim.main.notice.NoticesInCategoryScreen
import com.doyoonkim.main.preference.CustomerServiceScreen
import com.doyoonkim.main.preference.NotificationPreferencesScreen
import com.doyoonkim.main.preference.OssNoticeScreen
import com.doyoonkim.main.preference.UserPreferenceScreen
import com.doyoonkim.main.tip.TipDetailScreen
import com.doyoonkim.main.viewmodel.CustomerServiceViewModel
import com.doyoonkim.main.viewmodel.HomeViewModel
import com.doyoonkim.main.viewmodel.NoticeByMajorViewModel
import com.doyoonkim.main.viewmodel.NoticeDetailViewModel
import com.doyoonkim.main.viewmodel.NoticeSearchViewModel
import com.doyoonkim.main.viewmodel.NoticesInCategoryViewModel
import com.doyoonkim.main.viewmodel.NotificationPreferencesViewModel
import com.doyoonkim.main.viewmodel.SettingsViewModel
import com.doyoonkim.model.NoticeCategory
import com.google.firebase.analytics.FirebaseAnalytics

fun NavGraphBuilder.mainServiceNavGraph(
    navController: NavController,
    appComponent: AppComponent,
    contentPadding: PaddingValues,
    onNoticeDetailRequested: (NoticeDetail) -> Unit,
    onBookmarkServiceRequested: (BookmarkInfo) -> Unit,
    onExit: () -> Unit = {  }
) {
    val analytics = appComponent.analytics()

    // ViewModels will be injected via ViewModelFactory
    composable(NavRoutes.Home.route) {
        val sceneComponent = remember(appComponent) {
            DaggerHomeSceneComponent.factory().create(DefaultSystemService(appComponent))
        }

        HomeDashboard(
            modifier = Modifier.padding(horizontal = 5.dp),
            viewModel = viewModel<HomeViewModel>(factory = sceneComponent.viewModelFactory()),
            bottomPadding = contentPadding.calculateBottomPadding(),
            onSettingsRequested = { navController.navigate(NavRoutes.Settings.route) },
            onGoBackAction = {
                navController.popBackStack().also { if (!it) onExit() }
            },
            onMoreNoticeRequested = { dest ->
                navController.run {
                    when (dest) {
                        Destination.MORE_GENERAL -> navigate(NavRoutes.GeneralNotices.route)
                        Destination.MORE_ACADEMIC -> navigate(NavRoutes.AcademicNotices.route)
                        Destination.MORE_SCHOLARSHIP -> navigate(NavRoutes.ScholarshipNotices.route)
                        Destination.MORE_EVENT -> navigate(NavRoutes.EventNotices.route)
                        Destination.MORE_EMPLOYMENT -> navigate(NavRoutes.EmploymentNotices.route)
                        else -> { /* DO NOTHING. */
                        }
                    }
                }
            },
            onFullContentRequested = { id, url ->
                onNoticeDetailRequested(NoticeDetail(id, url))
            },
            onTipClicked = { category, url ->
                analytics.logEvent("BROWSE_TIP", Bundle().apply {
                    putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category.name)
                    putString(FirebaseAnalytics.Param.SOURCE, "HomeScreen")
                    putString(FirebaseAnalytics.Param.DESTINATION, url)
                })
                navController.navigate("tipDetail/${category.name}/${Uri.encode(url)}")
            },
            onMoreMajorNoticeRequested = {
                navController.navigate(NavRoutes.MajorNotices.route)
            },
        )
    }

    composable(
        route = NavRoutes.MajorNotices.route
    ) {
        val sceneComponent = remember(appComponent) {
            DaggerNoticeByMajorSceneComponent.factory().create(DefaultSystemService(appComponent))
        }

        NoticeByMajorScreen(
            modifier = Modifier,
            viewModel = viewModel<NoticeByMajorViewModel>(factory = sceneComponent.getViewModelFactory()),
            bottomPadding = contentPadding.calculateBottomPadding(),
            onGoBackRequested = { navController.popBackStack() },
            onSettingRequested = { navController.navigate(NavRoutes.Settings.route) },
            onNoticeDetailRequested = { id, url -> onNoticeDetailRequested(NoticeDetail(id, url)) }
        )
    }

    composable(
        route = NavRoutes.NoticeSearch.route
    ) {
        val sceneComponent = remember(appComponent) {
            DaggerNoticeSearchSceneComponent.factory().create(DefaultSystemService(appComponent))
        }

        NoticeSearchScreen(
            modifier = Modifier,
            viewModel = viewModel<NoticeSearchViewModel>(factory = sceneComponent.viewModelFactory()),
            bottomPadding = contentPadding.calculateBottomPadding(),
            onBackPressed = { navController.popBackStack() },
            onNoticeSelected = { id, url ->
                onNoticeDetailRequested(NoticeDetail(id, url))
            },
            onBookmarkSelected = { id, title, category ->
                onBookmarkServiceRequested(
                    BookmarkInfo(
                        noticeId = id,
                        noticeTitle = title,
                        noticeInfo = category
                    )
                )
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
        val sceneComponent = remember(appComponent) {
            DaggerNoticeInCategorySceneComponent.factory().create(DefaultSystemService(appComponent))
        }

        NoticesInCategoryScreen(
            modifier = Modifier,
            category = NoticeCategory.GENERAL_NEWS,
            viewModel = viewModel<NoticesInCategoryViewModel>(factory = sceneComponent.viewModelFactory()),
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
        val sceneComponent = remember(appComponent) {
            DaggerNoticeInCategorySceneComponent.factory().create(DefaultSystemService(appComponent))
        }

        NoticesInCategoryScreen(
            modifier = Modifier,
            category = NoticeCategory.ACADEMIC_NEWS,
            viewModel = viewModel<NoticesInCategoryViewModel>(factory = sceneComponent.viewModelFactory()),
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
        val sceneComponent = remember(appComponent) {
            DaggerNoticeInCategorySceneComponent.factory().create(DefaultSystemService(appComponent))
        }

        NoticesInCategoryScreen(
            modifier = Modifier,
            category = NoticeCategory.SCHOLARSHIP_NEWS,
            viewModel = viewModel<NoticesInCategoryViewModel>(factory = sceneComponent.viewModelFactory()),
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
        val sceneComponent = remember(appComponent) {
            DaggerNoticeInCategorySceneComponent.factory().create(DefaultSystemService(appComponent))
        }

        NoticesInCategoryScreen(
            modifier = Modifier,
            category = NoticeCategory.EVENT_NEWS,
            viewModel = viewModel<NoticesInCategoryViewModel>(factory = sceneComponent.viewModelFactory()),
            onBackButtonPressed = { navController.popBackStack() },
            onNoticeSelected = { id, url ->
                onNoticeDetailRequested(NoticeDetail(id, url))
            }
        )
    }

    composable(
        route = NavRoutes.EmploymentNotices.route,
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
            DaggerNoticeInCategorySceneComponent.factory().create(DefaultSystemService(appComponent))
        }

        NoticesInCategoryScreen(
            modifier = Modifier,
            category = NoticeCategory.EMPLOYMENT_NEWS,
            viewModel = viewModel<NoticesInCategoryViewModel>(factory = sceneComponent.viewModelFactory()),
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
        val sceneComponent = remember(appComponent) {
            DaggerSettingsSceneComponent.factory().create(DefaultSystemService(appComponent))
        }

        UserPreferenceScreen(
            modifier = Modifier.padding(horizontal = 10.dp),
            viewModel = viewModel<SettingsViewModel>(factory = sceneComponent.viewModelFactory()),
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
        val sceneComponent = remember(appComponent) {
            DaggerNotificationPreferencesSceneComponent.factory().create(DefaultSystemService(appComponent))
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
            DaggerCustomerServiceSceneComponent.factory().create(DefaultSystemService(appComponent))
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

        val sceneComponent = remember(appComponent) {
            DaggerNoticeDetailSceneComponent.factory().create(DefaultSystemService(appComponent))
        }

        NoticeDetailScreen(
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel<NoticeDetailViewModel>(factory = sceneComponent.viewModelFactory()),
            noticeInfo = noticeInfo,
            onBookmarkCreate = { onBookmarkServiceRequested(BookmarkInfo(
                noticeId = it.nttId,
                noticeTitle = it.title,
                noticeInfo = it.noticeName.ifBlank { it.departName }
            )) },
            onBackPressed = { navController.popBackStack() }
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
}