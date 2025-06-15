package com.doyoonkim.main

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.navigation.Destination
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.common.navigation.NoticeDetail
import com.doyoonkim.main.home.HomeScreen
import com.doyoonkim.main.notice.NoticeDetailScreen
import com.doyoonkim.main.notice.NoticeSearchScreen
import com.doyoonkim.main.notice.NoticesInCategoryScreen
import com.doyoonkim.main.preference.CustomerServiceScreen
import com.doyoonkim.main.preference.NotificationPreferencesScreen
import com.doyoonkim.main.preference.OssNoticeScreen
import com.doyoonkim.main.preference.UserPreferenceScreen
import com.doyoonkim.main.viewmodel.CustomerServiceViewModel
import com.doyoonkim.main.viewmodel.HomeViewModel
import com.doyoonkim.main.viewmodel.NoticeDetailViewModel
import com.doyoonkim.main.viewmodel.NoticeSearchViewModel
import com.doyoonkim.main.viewmodel.NoticesInCategoryViewModel
import com.doyoonkim.main.viewmodel.NotificationPreferencesViewModel
import com.doyoonkim.model.NoticeCategory

fun NavGraphBuilder.mainServiceNavGraph(
    navController: NavController,
    viewModelFactory: ViewModelProvider.Factory,
    onNoticeDetailRequested: (NoticeDetail) -> Unit,
    onBookmarkServiceRequested: (BookmarkInfo) -> Unit
) {
    // ViewModels will be injected via ViewModelFactory
    composable(NavRoutes.Home.route) {
        HomeScreen(
            modifier = Modifier.padding(5.dp),
            viewModel = viewModel<HomeViewModel>(factory = viewModelFactory),
            onGoBackAction = { navController.popBackStack() },
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
                onNoticeDetailRequested(NoticeDetail(id, url)) }
        )
    }

    composable(NavRoutes.NoticeSearch.route) {
        NoticeSearchScreen(
            modifier = Modifier.padding(5.dp),
            viewModel = viewModel<NoticeSearchViewModel>(factory = viewModelFactory),
            onBackPressed = { navController.popBackStack() },
            onNoticeSelected = { id, url ->
                onNoticeDetailRequested(NoticeDetail(id, url))
            }
        )
    }

    composable(NavRoutes.GeneralNotices.route) {
        NoticesInCategoryScreen(
            modifier = Modifier.padding(5.dp),
            category = NoticeCategory.GENERAL_NEWS,
            viewModel = viewModel<NoticesInCategoryViewModel>(factory = viewModelFactory),
            onBackButtonPressed = { navController.popBackStack() },
            onNoticeSelected = { id, url ->
                onNoticeDetailRequested(NoticeDetail(id, url))
            }
        )
    }

    composable(NavRoutes.AcademicNotices.route) {
        NoticesInCategoryScreen(
            modifier = Modifier.padding(5.dp),
            category = NoticeCategory.ACADEMIC_NEWS,
            viewModel = viewModel<NoticesInCategoryViewModel>(factory = viewModelFactory),
            onBackButtonPressed = { navController.popBackStack() },
            onNoticeSelected = { id, url ->
                onNoticeDetailRequested(NoticeDetail(id, url))
            }
        )
    }

    composable(NavRoutes.ScholarshipNotices.route) {
        NoticesInCategoryScreen(
            modifier = Modifier.padding(5.dp),
            category = NoticeCategory.SCHOLARSHIP_NEWS,
            viewModel = viewModel<NoticesInCategoryViewModel>(factory = viewModelFactory),
            onBackButtonPressed = { navController.popBackStack() },
            onNoticeSelected = { id, url ->
                onNoticeDetailRequested(NoticeDetail(id, url))
            }
        )
    }

    composable(NavRoutes.EventNotices.route) {
        NoticesInCategoryScreen(
            modifier = Modifier.padding(5.dp),
            category = NoticeCategory.EVENT_NEWS,
            viewModel = viewModel<NoticesInCategoryViewModel>(factory = viewModelFactory),
            onBackButtonPressed = { navController.popBackStack() },
            onNoticeSelected = { id, url ->
                onNoticeDetailRequested(NoticeDetail(id, url))
            }
        )
    }

    // preferences
    composable(NavRoutes.Settings.route) {
        UserPreferenceScreen(
            modifier = Modifier.padding(5.dp),
            onNotificationPreferenceClicked = { navController.navigate(NavRoutes.NotificationPreferences.route) },
            onCustomerServiceClicked = { navController.navigate(NavRoutes.CustomerService.route) },
            onOssClicked = { navController.navigate(NavRoutes.OpenSource.route) },
            onBackPressed = { navController.popBackStack() }
        )
    }

    composable(NavRoutes.NotificationPreferences.route) {
        NotificationPreferencesScreen(
            modifier = Modifier.padding(5.dp),
            viewModel = viewModel<NotificationPreferencesViewModel>(factory = viewModelFactory),
            onBackPressed = { navController.popBackStack() }
        )
    }

    composable(NavRoutes.CustomerService.route) {
        CustomerServiceScreen(
            modifier = Modifier.padding(5.dp),
            viewModel = viewModel<CustomerServiceViewModel>(factory = viewModelFactory),
            onBackPressed = { navController.popBackStack() }
        )
    }

    composable(NavRoutes.OpenSource.route) {
        OssNoticeScreen(
            modifier = Modifier.padding(5.dp),
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
    )
    ) { backStackEntry ->
        val noticeInfo = backStackEntry.arguments?.let {
            Triple(
                it.getString("nttId")?.toInt() ?: 0,
                Uri.decode(it.getString("contentUrl") ?: ""),
                it.getString("isFabVisible").toBoolean() ?: false
            )
        } ?: Triple(0, "", false)

        NoticeDetailScreen(
            modifier = Modifier.padding(5.dp),
            viewModel = viewModel<NoticeDetailViewModel>(factory = viewModelFactory),
            noticeInfo = noticeInfo,
            onBookmarkCreate = { onBookmarkServiceRequested(BookmarkInfo(
                noticeId = it.nttId,
                noticeTitle = it.title,
                noticeInfo = "[${it.departName}] ${it.timestamp}"
            )) },
            onBackPressed = { navController.popBackStack() }
        )
    }
}