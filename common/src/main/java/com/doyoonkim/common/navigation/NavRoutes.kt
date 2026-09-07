package com.doyoonkim.common.navigation

sealed class NavRoutes(val route: String) {

    data object Home : NavRoutes(Destination.HOME.name)
    data object Bookmark : NavRoutes(Destination.BOOKMARKS.name)

    data object Settings : NavRoutes(Destination.SETTINGS.name)
    data object NotificationPreferences : NavRoutes(Destination.NOTIFICATION.name)
    data object CustomerService : NavRoutes(Destination.CS.name)
    data object OpenSource : NavRoutes(Destination.OSS.name)
    data object NoticeSearch : NavRoutes(Destination.SEARCH.name)

    data object CarrelStatus : NavRoutes("reading-room")
    data object DiningMenu : NavRoutes("meal")

    // Dynamic Notice List Destination
    data object NoticeList : NavRoutes("notices_list/{category}") {
        // Key value for access NavArgument
        const val SELECTED_CATEGORY = "category"

        fun createRoute(category: String) = "notices_list/$category"
    }

    // TODO: Dest with Different category treated as different destination. --> Should be considered as same destination.
    data object NoticeListDashboard : NavRoutes("notices_dashboard/{category}") {
        const val SELECTED_CATEGORY = "category"
        fun createRoute(category: String) = "notices_dashboard/$category"
    }
}

enum class Destination {
    HOME,
    MORE_GENERAL,
    MORE_ACADEMIC,
    MORE_SCHOLARSHIP,
    MORE_EVENT,
    MORE_EMPLOYMENT,
    SETTINGS,
    OSS,
    CS,
    SEARCH,
    NOTIFICATION,
    BOOKMARKS
}
