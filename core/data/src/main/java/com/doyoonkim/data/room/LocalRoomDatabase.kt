package com.doyoonkim.data.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.doyoonkim.data.model.Bookmark
import com.doyoonkim.data.model.NoticeEntity

// Migration Strategy: https://stackoverflow.com/questions/56478785/room-database-schema-update-without-data-loss

@Database(
    entities = [Bookmark::class, NoticeEntity::class],
    version = 2
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun getDao(): MainDatabaseDao

    // Migration Code
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Bookmark ADD COLUMN created_at INTEGER DEFAULT 0 NOT NULL")
                db.execSQL("ALTER TABLE NoticeEntity ADD COLUMN notice_category TEXT DEFAULT ' ' NOT NULL")
                db.execSQL("ALTER TABLE Bookmark ADD COLUMN updated_at INTEGER DEFAULT 0 NOT NULL")
            }
        }
    }
}