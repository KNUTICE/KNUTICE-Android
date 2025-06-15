package com.doyoonkim.knutice

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.doyoonkim.bookmark.bookmarkServiceGraph
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.main.mainServiceNavGraph

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModelFactory: ViewModelProvider.Factory
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavRoutes.Home.route
    ) {
        mainServiceNavGraph(
            navController = navController,
            viewModelFactory = viewModelFactory,
            onNoticeDetailRequested = { target ->
                navController.navigate("noticeDetail/${target.nttId}/${Uri.encode(target.contentUrl)}/${target.isFabVisible}")
            },
            onBookmarkServiceRequested = {
                navController.navigate("bookmark/${it.noticeId}/${it.noticeTitle}/${it.noticeInfo}")
            }
        )

        bookmarkServiceGraph(
            navController = navController,
            viewModelFactory = viewModelFactory,
            onNoticeDetailRequested = { target ->
                navController.navigate("noticeDetail/${target.nttId}/${Uri.encode(target.contentUrl)}/${target.isFabVisible}")
            }
        )
    }
}