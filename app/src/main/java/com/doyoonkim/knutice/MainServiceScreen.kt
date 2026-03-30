package com.doyoonkim.knutice

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.doyoonkim.common.R
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.secondaryBackground
import com.doyoonkim.common.theme.title
import com.doyoonkim.common.ui.AnimatedBottomBar
import com.doyoonkim.common.ui.BottomBarButton
import com.doyoonkim.common.ui.LocalHomeSafeBottomPadding
import com.doyoonkim.knutice.navigation.AppNavHost
import com.doyoonkim.knutice.navigation.GraphRoute
import com.doyoonkim.knutice.navigation.rememberBottomNavigationState

@Composable
fun MainServiceScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onExit: () -> Unit
) {
    val backStackEntryState by navController.currentBackStackEntryAsState()
    val bottomBarSelectionState = remember(backStackEntryState) {
        when (backStackEntryState?.destination?.route) {
            NavRoutes.Home.route -> 0
            NavRoutes.MajorNotices.route -> 1
            NavRoutes.Bookmark.route -> 2
            NavRoutes.NoticeSearch.route -> 3
            else -> -1
        }
    }

    // Navigation via BottomNavBar handling
    val bottomNavigationState = rememberBottomNavigationState(
        navController, GraphRoute.MAIN, onExit
    )

    val isDarkTheme = isSystemInDarkTheme()
    val scrimColor = if (isDarkTheme) Color.Black else Color.White

    val bottomBarNavItems = remember {
        listOf<BottomBarButton>(
            BottomBarButton(
                R.string.bottom_bar_home,
                R.drawable.baseline_home_24,
                GraphRoute.MAIN
            ),
            BottomBarButton(
                R.string.bottom_bar_major_title,
                R.drawable.outline_school_24,
                GraphRoute.NOTICE
            ),
            BottomBarButton(
                R.string.bottom_bar_bookmark,
                R.drawable.baseline_bookmarks_24,
                GraphRoute.BOOKMARK
            ),
            BottomBarButton(
                R.string.title_search,
                R.drawable.baseline_search_24,
                GraphRoute.SEARCH
            )
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { contentPadding ->
        // Calculate Content-Safe bottom padding
        val navBarPadding = contentPadding.calculateBottomPadding() + 7.dp
        val contentSafeBottomPadding = 100.dp

        CompositionLocalProvider(
            LocalHomeSafeBottomPadding provides contentSafeBottomPadding
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .padding(
                        start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = contentPadding.calculateEndPadding(LayoutDirection.Ltr)
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                AppNavHost(
                    modifier = Modifier
                        .align(Alignment.Center),
                    contentPadding = PaddingValues(
                        top = contentPadding.calculateTopPadding(),
                        bottom = contentPadding.calculateBottomPadding() + 100.dp
                    ),
                    navController = navController,
                    onHomeTabSwitched = bottomNavigationState.navigateToBottomNavDestination,
                    onPopBottomNavHistory = bottomNavigationState.popHistory
                )

                if (bottomBarSelectionState >= 0) {
                    // Blur Effect in Overlapping Area
                    Spacer(
                        modifier = Modifier.fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .height(80.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        scrimColor.copy(alpha = 0.6f),
                                        scrimColor.copy(alpha = 0.8f),
                                        scrimColor.copy(alpha = 1f)
                                    )
                                )
                            )
                    )

                    AnimatedBottomBar(
                        modifier = Modifier
                            .padding(
                                start = 15.dp,
                                end = 15.dp,
                                bottom = navBarPadding
                            )
                            .align(Alignment.BottomCenter),
                        items = bottomBarNavItems,
                        selection = bottomBarSelectionState,
                        containerColor = MaterialTheme.colorScheme.secondaryBackground,
                        contentColor = MaterialTheme.colorScheme.title,
                        colorOnSelect = MaterialTheme.colorScheme.onAnyBackground,
                        onItemClicked = { _, dest ->
                            bottomNavigationState.switchTab(dest)
                        }
                    )
                }
            }
        }
    }
}