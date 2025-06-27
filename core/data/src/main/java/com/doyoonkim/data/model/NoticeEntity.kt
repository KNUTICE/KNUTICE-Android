package com.doyoonkim.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoticeEntity(
    @PrimaryKey(autoGenerate = true) val noticeEntityId: Int = 0,
    @ColumnInfo("ntt_id") val nttId: Int = -1,
    @ColumnInfo("notice_title") val title: String,
    @ColumnInfo("notice_url") val url: String,
    @ColumnInfo("notice_image") val imageUrl: String,
    @ColumnInfo("info_dept") val departName: String,
    @ColumnInfo("info_timestamp") val timestamp: String,
    @ColumnInfo("notice_category") val noticeCategory: String
)
