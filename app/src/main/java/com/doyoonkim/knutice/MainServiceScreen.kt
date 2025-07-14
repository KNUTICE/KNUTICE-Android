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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.doyoonkim.common.R
import com.doyoonkim.common.navigation.NavRoutes
import com.doyoonkim.common.theme.displayBackground
import com.doyoonkim.common.theme.onAnyBackground
import com.doyoonkim.common.theme.subTitle
import com.doyoonkim.common.theme.title
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun MainServiceScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModelFactory: ViewModelProvider.Factory,
    onExit: () -> Unit
) {

    var bottomBarSelectionState: Triple<Boolean, Boolean, Boolean>
    val backStackEntryState by navController.currentBackStackEntryAsState()
    backStackEntryState?.destination?.route.let {
        bottomBarSelectionState = when(it) {
            NavRoutes.Home.route -> Triple(true, false, false)
            NavRoutes.Bookmark.route -> Triple(false, true, false)
            NavRoutes.NoticeSearch.route -> Triple(false, false, true)
            else -> Triple(false, false, false)
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
                            selected = bottomBarSelectionState.first,
                            enabled = true,
                            onClick = {
                                if (!bottomBarSelectionState.first) {
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
                            selected = bottomBarSelectionState.second,
                            enabled = true,
                            onClick = {
                                if (!bottomBarSelectionState.second) {
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
                            selected = bottomBarSelectionState.third,
                            enabled = true,
                            onClick = {
                                if (!bottomBarSelectionState.third) {
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
            viewModelFactory = viewModelFactory,
            onExit = onExit
        )
    }
}

fun Triple<Boolean, Boolean, Boolean>.atLeastOneSelected() = this.first || this.second || this.third