package com.doyoonkim.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.doyoonkim.data.model.Bookmark
import com.doyoonkim.data.model.NoticeEntity

// Migration Strategy: https://stackoverflow.com/questions/56478785/room-database-schema-update-without-data-loss

@Database(entities = [Bookmark::class, NoticeEntity::class], version = 1)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun getDao(): MainDatabaseDao
}