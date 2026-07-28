package com.doyoonkim.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import com.doyoonkim.model.StagingPolicy

@Entity
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val bookmarkId: Int = 0,
    @ColumnInfo("isScheduled") val isScheduled: Boolean = false,
    @ColumnInfo("remind_schedule") val reminderSchedule: Long = 0,
    @ColumnInfo("bookmark_note") val note: String = "",
    @ColumnInfo("target_ntt_id") val nttId: Int = -1,
    @ColumnInfo("created_at") val createdAt: Long = 0,
    @ColumnInfo("updated_at") val updatedAt: Long = 0
)

@Fts4
@Entity
data class BookmarkFts(
    // internally created rowid should be matched with bookmark id
    @PrimaryKey @ColumnInfo("rowid") val id: Int,
    val bookmarkNotes: String,
    val noticeTitle: String,
    val bookmarkNoteTokenized: String,
    val noticeTitleTokenized: String
)

// Temporary Table for Asynchronous FTS table insertion
@Entity
data class PendingBookmarkFtsAsync(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(defaultValue = "0") val stagingId: Int = 0,
    val bookmarkId: Int,
    val bookmarkNotes: String,
    val noticeTitle: String,
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "INSERT")val stagingPolicy: StagingPolicy
)
