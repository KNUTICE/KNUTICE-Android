package com.doyoonkim.knutice.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoticeEntity(
    @PrimaryKey(autoGenerate = true) val noticeEntityId: Int,
    @ColumnInfo("ntt_id") val nttId: Int = -1,
    @ColumnInfo("notice_title") val title: String,
    @ColumnInfo("notice_url") val url: String,
    @ColumnInfo("notice_image") val imageUrl: String,
    @ColumnInfo("info_dept") val departName: String,
    @ColumnInfo("info_timestamp") val timestamp: String
) {
    fun toNotice(): Notice {
        return Notice(nttId, title, url, imageUrl, departName, timestamp)
    }
}

@Entity
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val bookmarkId: Int,
    @ColumnInfo("isScheduled") val isScheduled: Boolean = false,
    @ColumnInfo("remind_schedule") val reminderSchedule: Long = 0,
    @ColumnInfo("bookmark_note") val note: String = "",
    @ColumnInfo("target_ntt_id") val nttId: Int = -1
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