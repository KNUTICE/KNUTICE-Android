package com.doyoonkim.model

enum class StagingPolicy { INSERT, UPDATE, DELETE }

data class PendingBookmarkFtsVO(
    val stagingId: Int = 0,
    val bookmarkId: Int,
    val notes: String,
    val title: String,
    val policy: StagingPolicy
)
