package com.doyoonkim.bookmark

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.doyoonkim.bookmark.edit.EditBookmarkScreen
import com.doyoonkim.bookmark.list.BookmarkListScreen
import com.doyoonkim.bookmark.viewmodel.BookmarkListViewModel
import com.doyoonkim.bookmark.viewmodel.EditBookmarkViewModel
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.common.navigation.NoticeDetail
import com.doyoonkim.common.theme.displayBackground

fun NavGraphBuilder.bookmarkServiceGraph(
    navController: NavController,
    viewModelFactory: ViewModelProvider.Factory,
    contentPadding: PaddingValues,
    onNoticeDetailRequested: (NoticeDetail) -> Unit,
    onBookmarkRequested: (BookmarkInfo) -> Unit,
    onExit: () -> Unit = {  }
) {

    composable(NavRoutes.Bookmark.route) {
        BookmarkListScreen(
            modifier = Modifier.padding(horizontal = 5.dp),
            viewModel = viewModel<BookmarkListViewModel>(factory = viewModelFactory),
            bottomPadding = contentPadding.calculateBottomPadding(),
            onSettingsRequested = { navController.navigate(NavRoutes.Settings.route) },
            onBookmarkSelected = {
                onBookmarkRequested(it)
            },
            onBackPressed = {
                navController.popBackStack().also { if (!it) onExit() }
            }
        )
    }

    composable(
        route = "bookmark/{id}/{title}/{info}",
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "knutice://service/bookmark/{id}/{title}/{info}"
            }
        ),
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

        EditBookmarkScreen(
            modifier = Modifier.padding(horizontal = 10.dp),
            viewModel = viewModel<EditBookmarkViewModel>(factory = viewModelFactory),
            bookmarkInfo = bookmarkInfo,
            onNoticeSelected = { onNoticeDetailRequested(it) },
            onCompleted = {
                navController.navigate(NavRoutes.Bookmark.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            },
            onBackPressed = {
                navController.popBackStack().also {
                    if (!it) navController.navigate(NavRoutes.Bookmark.route)
                }
            }
        )
    }

}