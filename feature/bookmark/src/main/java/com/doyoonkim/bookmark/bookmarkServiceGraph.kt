package com.doyoonkim.bookmark

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
import com.doyoonkim.bookmark.edit.EditBookmarkScreen
import com.doyoonkim.bookmark.list.BookmarkListScreen
import com.doyoonkim.bookmark.viewmodel.BookmarkListViewModel
import com.doyoonkim.bookmark.viewmodel.EditBookmarkViewModel
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.common.navigation.NoticeDetail

fun NavGraphBuilder.bookmarkServiceGraph(
    navController: NavController,
    viewModelFactory: ViewModelProvider.Factory,
    onNoticeDetailRequested: (NoticeDetail) -> Unit
) {

    composable(NavRoutes.Bookmark.route) {
        BookmarkListScreen(
            modifier = Modifier.padding(5.dp),
            viewModel = viewModel<BookmarkListViewModel>(factory = viewModelFactory),
            onBookmarkSelected = { navController.navigate(it) },
            onBackPressed = { navController.popBackStack() }
        )
    }

    composable(
        route = "bookmark/{id}/{title}/{info}",
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "knutice://service/bookmark/{id}/{title}/{info}"
            }
        )
    ) { backStackEntry ->
        val bookmarkInfo = backStackEntry.arguments?.let {
            BookmarkInfo(
                it.getString("id")?.toInt() ?: 0,
                it.getString("title") ?: "",
                it.getString("info") ?: ""
            )
        } ?: BookmarkInfo(0, "", "")

        EditBookmarkScreen(
            modifier = Modifier.padding(5.dp),
            viewModel = viewModel<EditBookmarkViewModel>(factory = viewModelFactory),
            bookmarkInfo = bookmarkInfo,
            onNoticeSelected = { onNoticeDetailRequested(it) },
            onBackPressed = { navController.navigate(NavRoutes.Bookmark.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            } }
        )
    }

}