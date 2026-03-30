package com.doyoonkim.knutice.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.common.navigation.NoticeDetail
import com.doyoonkim.knutice.di.components.AppComponent
import com.doyoonkim.knutice.di.components.DaggerNoticeByMajorSceneComponent
import com.doyoonkim.knutice.di.components.DaggerNoticeDetailSceneComponent
import com.doyoonkim.knutice.di.components.DaggerNoticeInCategorySceneComponent
import com.doyoonkim.main.notice.NoticeByMajorScreen
import com.doyoonkim.main.notice.NoticeDetailScreen
import com.doyoonkim.main.notice.NoticesInCategoryScreen
import com.doyoonkim.main.viewmodel.NoticeByMajorViewModel
import com.doyoonkim.main.viewmodel.NoticeDetailViewModel
import com.doyoonkim.main.viewmodel.NoticesInCategoryViewModel
import com.doyoonkim.model.NoticeCategory

fun NavGraphBuilder.noticeServiceGraph(
    navController: NavController,
    appComponent: AppComponent,
    onNoticeDetailRequested: (NoticeDetail) -> Unit,
    onBookmarkServiceRequested: (BookmarkInfo) -> Unit,
    onPopBottomNavHistory: () -> Unit = {  }
) {
    composable(
        route = NavRoutes.MajorNotices.route
    ) {
        val sceneComponent = remember(appComponent) {
            DaggerNoticeByMajorSceneComponent.factory().create(
                systemServices = appComponent,
                networkProvider = appComponent,
                localPreferenceProvider = appComponent
            )
        }

        NoticeByMajorScreen(
            modifier = Modifier,
            viewModel = viewModel<NoticeByMajorViewModel>(factory = sceneComponent.getViewModelFactory()),
            onGoBackRequested = { onPopBottomNavHistory() },
            onSettingRequested = { navController.navigate(NavRoutes.Settings.route) },
            onNoticeDetailRequested = { id, url -> onNoticeDetailRequested(NoticeDetail(id, url)) }
        )
    }

    composable(
        route = NavRoutes.NoticeList.route,
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
            DaggerNoticeInCategorySceneComponent.factory().create(
                systemServices = appComponent,
                networkProvider = appComponent
            )
        }

        // Get Category value from NavArgument
        val selectedCategory = it.arguments?.getString(
            NavRoutes.NoticeList.SELECTED_CATEGORY
        ) ?: NoticeCategory.GENERAL_NEWS.name

        // Temporary Implementation. (Revised in 1.7.2)
        val target = try {
            NoticeCategory.valueOf(selectedCategory)
        } catch (e: Exception) {
            NoticeCategory.GENERAL_NEWS
        }

        NoticesInCategoryScreen(
            modifier = Modifier,
            category = target,
            viewModel = viewModel<NoticesInCategoryViewModel>(factory = sceneComponent.viewModelFactory()),
            onBackButtonPressed = { navController.popBackStack() },
            onNoticeSelected = { id, url ->
                onNoticeDetailRequested(NoticeDetail(id, url))
            }
        )

    }

    // DeepLinks for NoticeDetailScreen
    // notice?nttId=1234&contentUrl=https://ut.ac.kr/...&FabVisible=true
    composable(
        route = "notice?nttId={nttId}&contentUrl={contentUrl}&FabVisible={isFabVisible}",
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
            DaggerNoticeDetailSceneComponent.factory().create(
                systemServices = appComponent,
                networkProvider = appComponent,
                firebaseInfrastructureProvider = appComponent
            )
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
}