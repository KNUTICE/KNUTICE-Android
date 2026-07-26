package com.doyoonkim.data.room

import android.content.Context
import android.util.Log
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.doyoonkim.data.model.Bookmark
import com.doyoonkim.data.model.BookmarkFts
import com.doyoonkim.data.model.NoticeEntity
import com.doyoonkim.data.model.PendingBookmarkFtsAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

// Migration Strategy: https://stackoverflow.com/questions/56478785/room-database-schema-update-without-data-loss

@Database(
    entities = [Bookmark::class, NoticeEntity::class, BookmarkFts::class, PendingBookmarkFtsAsync::class],
    version = 5,
    autoMigrations = [ AutoMigration(from = 2, to = 3), AutoMigration(from = 3, to = 4), AutoMigration(from = 4, to = 5) ]
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun getDao(): MainDatabaseDao

    // Migration Code
    companion object {
        private var INSTANCE: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase {
            // Apply Custom Singleton logic.
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    LocalDatabase::class.java,
                    "Main Local Database"
                ).setQueryCallback(
                    { sqlQuery, bindArgs ->
                        Log.d("SQL", "Query: $sqlQuery SQLArgs: $bindArgs")
                    },
                    Dispatchers.IO.asExecutor()
                )
            }.addMigrations(
                MIGRATION_1_2
            ).build()
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Bookmark ADD COLUMN created_at INTEGER DEFAULT 0 NOT NULL")
                db.execSQL("ALTER TABLE NoticeEntity ADD COLUMN notice_category TEXT DEFAULT ' ' NOT NULL")
                db.execSQL("ALTER TABLE Bookmark ADD COLUMN updated_at INTEGER DEFAULT 0 NOT NULL")
            }
        }
    }
}
