package com.doyoonkim.knutice

import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
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
    contentPadding: PaddingValues,
    navController: NavHostController,
    viewModelFactory: ViewModelProvider.Factory
) {
    NavHost(
        modifier = modifier.padding(
            PaddingValues(
                top = contentPadding.calculateTopPadding(),
//                bottom = contentPadding.calculateBottomPadding()
            )
        ),
        navController = navController,
        startDestination = NavRoutes.Home.route
    ) {
        mainServiceNavGraph(
            navController = navController,
            viewModelFactory = viewModelFactory,
            contentPadding = contentPadding,
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
            contentPadding = contentPadding,
            onNoticeDetailRequested = { target ->
                navController.navigate("noticeDetail/${target.nttId}/${Uri.encode(target.contentUrl)}/${target.isFabVisible}")
            }
        )
    }
}