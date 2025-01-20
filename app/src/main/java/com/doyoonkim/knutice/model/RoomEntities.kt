package com.doyoonkim.knutice.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoticeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo("ntt_id") val nttId: Int = -1,
    @ColumnInfo("notice_title") val title: String,
    @ColumnInfo("notice_url") val url: String,
    @ColumnInfo("notice_image") val imageUrl: String,
    @ColumnInfo("info_dept") val departName: String,
    @ColumnInfo("info_timestamp") val timestamp: String
)

@Entity
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val bookmarkId: Int,
    @ColumnInfo("bookmarked_notice") val noticeNttId: String,
    @ColumnInfo("isScheduled") val isScheduled: Boolean,
    @ColumnInfo("remind_schedule") val reminderSchedule: String,
    @ColumnInfo("bookmark_note") val note: String
)

/*
data class Notice(
    val nttId: Int = -1,
    val title: String = "Unknown",
    val url: String = "Unknown",
    val imageUrl: String = "",
    val departName: String = "Unknown",
    val timestamp: String = "Unknown"
)
 */