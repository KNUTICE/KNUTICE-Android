package com.doyoonkim.common.navigation


sealed class NavRoutes(val route: String) {

    data object Home : NavRoutes(Destination.HOME.name)
    data object Bookmark: NavRoutes(Destination.BOOKMARKS.name)
    data object MajorNotices: NavRoutes(Destination.MAJOR_NOTICES.name)

    data object Settings: NavRoutes(Destination.SETTINGS.name)
    data object NotificationPreferences: NavRoutes(Destination.NOTIFICATION.name)
    data object CustomerService: NavRoutes(Destination.CS.name)
    data object OpenSource: NavRoutes(Destination.OSS.name)

    data object GeneralNotices: NavRoutes(Destination.MORE_GENERAL.name)
    data object AcademicNotices: NavRoutes(Destination.MORE_ACADEMIC.name)
    data object ScholarshipNotices: NavRoutes(Destination.MORE_SCHOLARSHIP.name)
    data object EventNotices: NavRoutes(Destination.MORE_EVENT.name)
    data object EmploymentNotices: NavRoutes(Destination.MORE_EMPLOYMENT.name)

    data object NoticeSearch: NavRoutes(Destination.SEARCH.name)

    data object CarrelStatus: NavRoutes("reading-room")
    data object DiningMenu: NavRoutes("meal")
}

enum class Destination {
    HOME, MORE_GENERAL, MORE_ACADEMIC, MORE_SCHOLARSHIP, MORE_EVENT, MORE_EMPLOYMENT,
    SETTINGS, OSS, CS, SEARCH, NOTIFICATION, BOOKMARKS, MAJOR_NOTICES
}