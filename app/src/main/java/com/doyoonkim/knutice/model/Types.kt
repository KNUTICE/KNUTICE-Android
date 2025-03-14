package com.doyoonkim.knutice.model

import kotlinx.serialization.Serializable

enum class NoticeCategory { GENERAL_NEWS, ACADEMIC_NEWS, SCHOLARSHIP_NEWS, EVENT_NEWS, Unspecified }

enum class Destination { MAIN, MORE_GENERAL, MORE_ACADEMIC, MORE_SCHOLARSHIP, MORE_EVENT, DETAILED,
    SETTINGS, OSS, CS, SEARCH, NOTIFICATION, BOOKMARKS, EDIT_BOOKMARK, Unspecified }

// Navigation Destinations
@Serializable
data class NavDestination(
    val arrived: Destination = Destination.Unspecified
)

@Serializable
data class FullContent(
    val title: String? = null,
    val info: String? = null,
    val url: String,
    val imgUrl: String
)