package com.doyoonkim.knutice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.doyoonkim.common.R
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import com.doyoonkim.knutice.navigation.AppNavHost

@Composable
fun MainServiceScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onExit: () -> Unit
) {

    var bottomBarSelectionState: BooleanArray
    val backStackEntryState by navController.currentBackStackEntryAsState()
    backStackEntryState?.destination?.route.let {
        bottomBarSelectionState = when(it) {
            NavRoutes.Home.route -> booleanArrayOf(true, false, false, false)
            NavRoutes.MajorNotices.route -> booleanArrayOf(false, true, false, false)
            NavRoutes.Bookmark.route -> booleanArrayOf(false, false, true, false)
            NavRoutes.NoticeSearch.route -> booleanArrayOf(false, false, false, true)
            else -> booleanArrayOf(false, false, false, false)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (bottomBarSelectionState.atLeastOneSelected()) {
                BottomAppBar(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(Color.Transparent)
                        .clip(RoundedCornerShape(15.dp)),
                    actions = {
                        // https://developer.android.com/develop/ui/compose/navigation#bottom-nav
                        BottomNavigationItem(
                            selected = bottomBarSelectionState[0],
                            enabled = true,
                            onClick = {
                                if (!bottomBarSelectionState[0]) {
                                    navController.navigate(NavRoutes.Home.route)
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_home_24),
                                    contentDescription = "Main",
                                    modifier = Modifier.padding(bottom = 5.dp)
                                )
                            },
                            label = {
                                Text(stringResource(R.string.bottom_bar_home))
                            },
                            selectedContentColor = MaterialTheme.colorScheme.title,
                            unselectedContentColor = MaterialTheme.colorScheme.subTitle
                        )
                        BottomNavigationItem(
                            selected = bottomBarSelectionState[1],
                            enabled = true,
                            onClick = {
                                if (!bottomBarSelectionState[1]) {
                                    navController.navigate(NavRoutes.MajorNotices.route)
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.outline_school_24),
                                    contentDescription = "Notice By Major",
                                    modifier = Modifier.padding(bottom = 5.dp)
                                )
                            },
                            label = {
                                Text("학과 공지")
                            },
                            selectedContentColor = MaterialTheme.colorScheme.title,
                            unselectedContentColor = MaterialTheme.colorScheme.subTitle
                        )

                        BottomNavigationItem(
                            selected = bottomBarSelectionState[2],
                            enabled = true,
                            onClick = {
                                if (!bottomBarSelectionState[2]) {
                                    navController.navigate(NavRoutes.Bookmark.route)
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_bookmarks_24),
                                    contentDescription = "Bookmarks",
                                    modifier = Modifier.padding(bottom = 5.dp)
                                )
                            },
                            label = {
                                Text(stringResource(R.string.bottom_bar_bookmark))
                            },
                            selectedContentColor = MaterialTheme.colorScheme.title,
                            unselectedContentColor = MaterialTheme.colorScheme.subTitle
                        )
                        BottomNavigationItem(
                            selected = bottomBarSelectionState[3],
                            enabled = true,
                            onClick = {
                                if (!bottomBarSelectionState[3]) {
                                    navController.navigate(NavRoutes.NoticeSearch.route)
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_search_24),
                                    contentDescription = "Search",
                                    modifier = Modifier.padding(bottom = 5.dp)
                                )
                            },
                            label = {
                                Text(stringResource(R.string.title_search))
                            },
                            selectedContentColor = MaterialTheme.colorScheme.title,
                            unselectedContentColor = MaterialTheme.colorScheme.subTitle
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.onAnyBackground
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.displayBackground
    ) { contentPadding ->
        AppNavHost(
            modifier = Modifier,
            contentPadding = contentPadding,
            navController = navController,
            onExit = onExit
        )
    }
}

fun Triple<Boolean, Boolean, Boolean>.atLeastOneSelected() = this.first || this.second || this.third

fun BooleanArray.atLeastOneSelected(): Boolean {
    this.forEach { if (it) return true }
    return false
}