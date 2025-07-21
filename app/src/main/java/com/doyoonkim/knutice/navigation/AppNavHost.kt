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
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.knutice.MainApplication

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    navController: NavHostController,
    onExit: () -> Unit = { /* ON EXIT HANDLING */ }
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
        startDestination = NavRoutes.Home.route
    ) {
        mainServiceNavGraph(
            navController = navController,
            appComponent = appComponent,
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
            appComponent = appComponent,
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