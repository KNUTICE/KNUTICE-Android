package com.doyoonkim.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val bookmarkId: Int = 0,
    @ColumnInfo("isScheduled") val isScheduled: Boolean = false,
    @ColumnInfo("remind_schedule") val reminderSchedule: Long = 0,
    @ColumnInfo("bookmark_note") val note: String = "",
    @ColumnInfo("target_ntt_id") val nttId: Int = -1,
    @ColumnInfo("created_at") val createdAt: Long = 0
)
