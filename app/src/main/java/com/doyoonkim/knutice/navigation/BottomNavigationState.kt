package com.doyoonkim.knutice.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

@Stable
class BottomNavigationState(
    val navController: NavController,
    initialDestination: String,
    onExit: () -> Unit = {  }
) {

    // Navigation via BottomNabBar Handling
    val tabHistory = mutableStateListOf(initialDestination)

    // Switch between tabs
    val switchTab: (String) -> Unit = { route ->
        // Check current BottomNavBar Tab History
        if (tabHistory.lastOrNull() != route) {
            tabHistory.add(route)

            // Perform Navigation
            navigate(route)
        }
    }

    // Manage Nav-Back
    val popHistory: () -> Unit = {
        if (tabHistory.size > 1) {
            tabHistory.removeAt(tabHistory.lastIndex)
            val targetDestination = tabHistory.last()

            // Perform Navigation
            navigate(targetDestination)
        } else {
            onExit()
        }
    }

    val navigateToBottomNavDestination: (String) -> Unit = { targetDest ->
        val currentTabRoot = tabHistory.lastOrNull()

        // Case where current destination is not in the scope of same NavGraph of targeted destination.
        if (currentTabRoot != null && currentTabRoot != targetDest) {
            // Clear BackStackEntries under the Current NavGraph.
            navController.popBackStack(
                route = currentTabRoot,
                inclusive = false
            )
            // Nav to the NavGraph of selected target destination. (Just like BottomNavButton event)
            switchTab(targetDest)
        } else {
            // Already in the scope of same NavGraph
            navController.popBackStack()
        }
    }

    private fun navigate(dest: String) {
        navController.navigate(dest) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

}

@Composable
fun rememberBottomNavigationState(
    navController: NavController,
    initialDestination: String,
    onExit: () -> Unit
): BottomNavigationState {
    return remember { BottomNavigationState(navController, initialDestination, onExit) }
}