package com.doyoonkim.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.doyoonkim.data.model.Bookmark
import com.doyoonkim.data.model.NoticeEntity

// Migration Strategy: https://stackoverflow.com/questions/56478785/room-database-schema-update-without-data-loss

@Database(entities = [Bookmark::class, NoticeEntity::class], version = 1)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun getDao(): MainDatabaseDao

    companion object {
        private var INSTANCE: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase {
            if (INSTANCE == null) {
                synchronized(LocalDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        LocalDatabase::class.java,
                        "Main Local Database"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}