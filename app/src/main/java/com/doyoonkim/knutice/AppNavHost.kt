package com.doyoonkim.knutice

import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
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
    viewModelFactory: ViewModelProvider.Factory,
    startDestination: String,
    onExit: () -> Unit = { /* ON EXIT HANDLING */ }
) {
    NavHost(
        modifier = modifier.padding(
            PaddingValues(
                start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = contentPadding.calculateEndPadding(LayoutDirection.Ltr)
            )
        ),
        navController = navController,
        startDestination = startDestination
    ) {
        mainServiceNavGraph(
            navController = navController,
            viewModelFactory = viewModelFactory,
            contentPadding = contentPadding,
            onNoticeDetailRequested = { target ->
                navController.navigate("noticeDetail/${target.nttId}/${Uri.encode(target.contentUrl)}/${target.isFabVisible}")
            },
            onBookmarkServiceRequested = {
                navController.navigate("bookmark/${it.noticeId}/${Uri.encode(it.noticeTitle)}/${Uri.encode(it.noticeInfo)}")
            },
            onExit = onExit
        )

        bookmarkServiceGraph(
            navController = navController,
            viewModelFactory = viewModelFactory,
            contentPadding = contentPadding,
            onNoticeDetailRequested = { target ->
                navController.navigate("noticeDetail/${target.nttId}/${Uri.encode(target.contentUrl)}/${target.isFabVisible}")
            },
            onBookmarkRequested = {
                navController.navigate("bookmark/${it.noticeId}/${Uri.encode(it.noticeTitle)}/${Uri.encode(it.noticeInfo)}")
            },
            onExit = onExit
        )
    }
}