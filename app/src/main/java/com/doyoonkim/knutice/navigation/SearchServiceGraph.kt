package com.doyoonkim.knutice.navigation

import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.doyoonkim.common.navigation.BookmarkInfo
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.common.navigation.NoticeDetail
import com.doyoonkim.knutice.di.components.AppComponent
import com.doyoonkim.knutice.di.components.DaggerNoticeSearchSceneComponent
import com.doyoonkim.main.notice.NoticeSearchScreen
import com.doyoonkim.main.viewmodel.NoticeSearchViewModel

fun NavGraphBuilder.searchServiceGraph(
    appComponent: AppComponent,
    onNoticeDetailRequested: (NoticeDetail) -> Unit,
    onBookmarkServiceRequested: (BookmarkInfo) -> Unit,
    onPopBottomNavHistory: () -> Unit = { }
) {
    composable(
        route = NavRoutes.NoticeSearch.route
    ) {
        val sceneComponent = remember(appComponent) {
            DaggerNoticeSearchSceneComponent.factory().create(
                systemServices = appComponent,
                networkProvider = appComponent,
                localStorageProvider = appComponent
            )
        }

        NoticeSearchScreen(
            modifier = Modifier,
            viewModel = viewModel<NoticeSearchViewModel>(factory = sceneComponent.viewModelFactory()),
            onBackPressed = { onPopBottomNavHistory() },
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
}
