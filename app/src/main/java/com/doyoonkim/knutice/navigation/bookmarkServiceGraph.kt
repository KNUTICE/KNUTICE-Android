package com.doyoonkim.knutice.navigation

import android.net.Uri
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
import com.doyoonkim.bookmark.edit.EditBookmarkScreen
import com.doyoonkim.bookmark.list.BookmarkListScreen
import com.doyoonkim.bookmark.viewmodel.BookmarkListViewModel
import com.doyoonkim.bookmark.viewmodel.EditBookmarkViewModel
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.common.navigation.NoticeDetail
import com.doyoonkim.knutice.di.components.AppComponent
import com.doyoonkim.knutice.di.components.DaggerBookmarkListSceneComponent
import com.doyoonkim.knutice.di.components.DaggerEditBookmarkSceneComponent

fun NavGraphBuilder.bookmarkServiceGraph(
    navController: NavController,
    appComponent: AppComponent,
    onNoticeDetailRequested: (NoticeDetail) -> Unit,
    onBookmarkRequested: (BookmarkInfo) -> Unit,
    onPopBottomNavHistory: () -> Unit = {  }
) {

    composable(NavRoutes.Bookmark.route) {
        val sceneComponent = remember(appComponent) {
            DaggerBookmarkListSceneComponent.factory().create(
                systemServices = appComponent,
                localPreferenceProvider = appComponent,
                localStorageProvider = appComponent
            )
        }

        BookmarkListScreen(
            modifier = Modifier.padding(horizontal = 5.dp),
            viewModel = viewModel<BookmarkListViewModel>(factory = sceneComponent.getViewModelFactory()),
            onSettingsRequested = { navController.navigate(NavRoutes.Settings.route) },
            onBookmarkSelected = {
                onBookmarkRequested(it)
            },
            onBackPressed = {
                onPopBottomNavHistory()
            }
        )
    }

    composable(
        route = "bookmark?id={id}&title={title}&info={info}",
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
        },
    ) { backStackEntry ->
        val bookmarkInfo = backStackEntry.arguments?.let {
            BookmarkInfo(
                it.getString("id")?.toInt() ?: 0,
                Uri.decode(it.getString("title")) ?: "",
                Uri.decode(it.getString("info")) ?: ""
            )
        } ?: BookmarkInfo(0, "", "")

        val sceneComponent = remember(appComponent) {
            DaggerEditBookmarkSceneComponent.factory().create(
                systemService = appComponent,
                networkProvider = appComponent,
                localStorageProvider = appComponent
            )
        }

        EditBookmarkScreen(
            modifier = Modifier.padding(horizontal = 10.dp),
            viewModel = viewModel<EditBookmarkViewModel>(factory = sceneComponent.viewModelFactory()),
            bookmarkInfo = bookmarkInfo,
            onNoticeSelected = { onNoticeDetailRequested(it) },
            onCompleted = {
                navController.popBackStack()
            },
            onBackPressed = {
                navController.popBackStack().also {
                    if (!it) navController.navigate(NavRoutes.Bookmark.route)
                }
            }
        )
    }

}