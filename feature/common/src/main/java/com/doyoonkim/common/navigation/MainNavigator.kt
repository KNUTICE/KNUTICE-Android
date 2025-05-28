package com.doyoonkim.common.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun MainNavigator(
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = Destination.MAIN.name
    ) {

    }

}

enum class Destination { MAIN, MORE_GENERAL, MORE_ACADEMIC, MORE_SCHOLARSHIP, MORE_EVENT, DETAILED,
    SETTINGS, OSS, CS, SEARCH, NOTIFICATION, BOOKMARKS, EDIT_BOOKMARK, Unspecified }