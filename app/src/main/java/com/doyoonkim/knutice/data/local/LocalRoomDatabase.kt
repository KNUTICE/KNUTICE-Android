package com.doyoonkim.knutice.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.doyoonkim.knutice.model.Bookmark
import com.doyoonkim.knutice.model.NoticeEntity


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
