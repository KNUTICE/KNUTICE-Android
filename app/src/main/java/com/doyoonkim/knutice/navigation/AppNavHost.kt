package com.doyoonkim.knutice.navigation

import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.common.navigation.NoticeDetail
import com.doyoonkim.knutice.MainApplication

// Nested NavGraph
object GraphRoute {
    const val MAIN = "main_service_graph"
    const val NOTICE = "notice_service_graph"
    const val BOOKMARK = "bookmark_service_graph"
    const val SEARCH = "search_service_graph"
    const val PREFERENCE = "preference_service_graph"
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    navController: NavHostController,
    onHomeTabSwitched: (String) -> Unit,
    onPopBottomNavHistory: () -> Unit = { /* ON EXIT HANDLING */ }
) {
    val appComponent = with(LocalContext.current) {
        remember { (this.applicationContext as MainApplication).appComponent }
    }

    NavHost(
        modifier = modifier.padding(
            PaddingValues(
                start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = contentPadding.calculateEndPadding(LayoutDirection.Ltr)
            )
        ),
        navController = navController,
        startDestination = GraphRoute.MAIN
    ) {
        navigation(
            route = GraphRoute.MAIN,
            startDestination = NavRoutes.Home.route
        ) {
            mainServiceNavGraph(
                navController = navController,
                appComponent = appComponent,
                onNoticeDetailRequested = { target ->
                    navController.navigate(target.toNavRoute())
                },
                onTabSwitches = onHomeTabSwitched,
                onPopBottomNavHistory = onPopBottomNavHistory
            )
        }

        navigation(
            route = GraphRoute.NOTICE,
            startDestination = NavRoutes.MajorNotices.route // Need to revise in 1.7.1 based on updated use case
        ) {
            noticeServiceGraph(
                navController = navController,
                appComponent = appComponent,
                onNoticeDetailRequested = { target ->
                    navController.navigate(target.toNavRoute())
                },
                onBookmarkServiceRequested = { target ->
                    navController.navigate(target.toNavRoute())
                },
                onPopBottomNavHistory = onPopBottomNavHistory
            )
        }

        navigation(
            route = GraphRoute.BOOKMARK,
            startDestination = NavRoutes.Bookmark.route
        ) {
            bookmarkServiceGraph(
                navController = navController,
                appComponent = appComponent,
                onNoticeDetailRequested = { target ->
                    navController.navigate(target.toNavRoute())
                },
                onBookmarkRequested = { target ->
                    navController.navigate(target.toNavRoute())
                },
                onPopBottomNavHistory = onPopBottomNavHistory
            )
        }

        navigation(
            route = GraphRoute.SEARCH,
            startDestination = NavRoutes.NoticeSearch.route
        ) {
            searchServiceGraph(
                appComponent = appComponent,
                onNoticeDetailRequested = { target ->
                    navController.navigate(target.toNavRoute())
                },
                onBookmarkServiceRequested = { target ->
                    navController.navigate(target.toNavRoute())
                },
                onPopBottomNavHistory = onPopBottomNavHistory
            )
        }

        navigation(
            route = GraphRoute.PREFERENCE,
            startDestination = NavRoutes.Settings.route
        ) {
            preferenceServiceGraph(
                navController = navController,
                appComponent = appComponent,
                navToBottomDest = onHomeTabSwitched
            )
        }
    }
}

// Notice Detail Destination Resolver
private fun NoticeDetail.toNavRoute() =
    "notice?nttId=${this.nttId}&contentUrl=${Uri.encode(this.contentUrl)}&FabVisible=${this.isFabVisible}"

// Bookmark Detail Destination Resolver
private fun BookmarkInfo.toNavRoute() =
    "bookmark?id=${this.noticeId}&title=${Uri.encode(this.noticeTitle)}&info=${Uri.encode(this.noticeInfo)}"
