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

@Composable
fun MainServiceScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModelFactory: ViewModelProvider.Factory,
    onExit: () -> Unit
) {

    var sharedScaffoldState: Triple<Boolean, Boolean, Boolean>
    val backStackEntryState by navController.currentBackStackEntryAsState()
    backStackEntryState?.destination?.route.let {
        sharedScaffoldState = when(it) {
            NavRoutes.Home.route -> Triple(true, true, false)
            NavRoutes.Bookmark.route -> Triple(true, false, true)
            else -> Triple(false, false, false)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (sharedScaffoldState.first) {
                BottomAppBar(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(Color.Transparent)
                        .clip(RoundedCornerShape(15.dp)),
                    actions = {
                        // https://developer.android.com/develop/ui/compose/navigation#bottom-nav
                        BottomNavigationItem(
                            selected = sharedScaffoldState.second,
                            enabled = true,
                            onClick = {
                                if (!sharedScaffoldState.second) {
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
                            selected = sharedScaffoldState.third,
                            enabled = true,
                            onClick = {
                                if (!sharedScaffoldState.third) {
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