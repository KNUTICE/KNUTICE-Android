package com.doyoonkim.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.doyoonkim.data.model.Bookmark
import com.doyoonkim.data.model.BookmarkAsListElement
import com.doyoonkim.data.model.BookmarkFts
import com.doyoonkim.data.model.BookmarkFtsTarget
import com.doyoonkim.data.model.NoticeEntity
import com.doyoonkim.data.model.PendingBookmarkFtsAsync

@Dao
interface MainDatabaseDao {
    @Query("SELECT * FROM Bookmark")
    fun getAllBookmarks(): List<Bookmark>

    @Query("SELECT * FROM Bookmark LIMIT :size OFFSET :pageNumber * :size")
    fun getBookmarksByPage(size: Int, pageNumber: Int): List<Bookmark>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createBookmark(entity: Bookmark)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createNoticeEntity(entity: NoticeEntity)

    @Update
    fun updateBookmark(updated: Bookmark)

    @Update
    fun updateNoticeEntity(updated: NoticeEntity)

    @Delete
    fun deleteBookmark(target: Bookmark)

    @Delete
    fun deleteNoticeEntity(target: NoticeEntity)

    // Fts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createBookmarkFts(entry: BookmarkFts)

    @Query(
        """
        DELETE FROM BookmarkFts WHERE rowid = :ftsId
    """
    )
    fun deleteBookmarkFts(ftsId: Int)

    // Temp Table for Asynchronous FTS table insertion
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createAsyncFtsEntity(entity: PendingBookmarkFtsAsync)

    @Query(
        """
        SELECT * FROM PendingBookmarkFtsAsync ORDER BY createdAt ASC LIMIT :limit
    """
    )
    fun getPendingBookmarkFtsAsyncBatch(limit: Int): List<PendingBookmarkFtsAsync>

    @Query(
        """
        DELETE FROM PendingBookmarkFtsAsync WHERE stagingId IN (:stagingIds)
    """
    )
    fun removePendingBookmarkFtsAsync(stagingIds: List<Int>)

    @Transaction
    fun updateBookmarkFts(
        id: Int,
        notes: String,
        title: String,
        notesTokenized: String,
        titleTokenized: String
    ) {
        // Based on Official SQL documentation, use DELETE followed by INSERT statement
        // would be used for updates.
        deleteBookmarkFts(id)
        createBookmarkFts(
            BookmarkFts(id, notes, title, notesTokenized, titleTokenized)
        )
    }

    @Query("SELECT * FROM NoticeEntity WHERE ntt_id=:nttId")
    fun getNoticeByNttId(nttId: Int): NoticeEntity?

    @Query("SELECT * FROM Bookmark WHERE target_ntt_id=:nttId")
    fun getBookmarkByNttId(nttId: Int): Bookmark?

    @Query("SELECT * FROM Bookmark ORDER BY bookmarkId DESC LIMIT :size OFFSET :pageNumber * :size")
    fun getBookmarkSortedNewest(size: Int, pageNumber: Int): List<Bookmark>

    @Query("SELECT * FROM Bookmark ORDER BY bookmarkId ASC LIMIT :size OFFSET :pageNumber * :size")
    fun getBookmarkSortedOldest(size: Int, pageNumber: Int): List<Bookmark>

    @Query(
        """
        SELECT
            b.bookmarkId AS bookmarkId,
            n.ntt_id AS noticeId,
            n.notice_title AS noticeTitle,
            n.notice_category AS noticeCategory,
            b.isScheduled AS isReminderSet,
            b.created_at AS createdAt,
            b.updated_at AS updatedAt
        FROM Bookmark b
        INNER JOIN NoticeEntity n ON n.ntt_id = b.target_ntt_id
        ORDER BY b.bookmarkId ASC LIMIT :size OFFSET :pageNumber * :size
    """
    )
    fun getBookmarkListSortedNewest(size: Int, pageNumber: Int): List<BookmarkAsListElement>

    @Query(
        """
        SELECT
            b.bookmarkId AS bookmarkId,
            n.ntt_id AS noticeId,
            n.notice_title AS noticeTitle,
            n.notice_category AS noticeCategory,
            b.isScheduled AS isReminderSet,
            b.created_at AS createdAt,
            b.updated_at AS updatedAt
        FROM Bookmark b
        INNER JOIN NoticeEntity n ON n.ntt_id = b.target_ntt_id
        ORDER BY b.bookmarkId DESC LIMIT :size OFFSET :pageNumber * :size
    """
    )
    fun getBookmarkListSortedOldest(size: Int, pageNumber: Int): List<BookmarkAsListElement>

    @Query(
        """
        SELECT
            b.bookmarkId AS bookmarkId,
            n.ntt_id AS noticeId,
            n.notice_title AS noticeTitle,
            n.notice_category AS noticeCategory,
            b.isScheduled AS isReminderSet,
            b.created_at AS createdAt,
            b.updated_at AS updatedAt 
        FROM Bookmark b
        INNER JOIN NoticeEntity n ON n.ntt_id = b.target_ntt_id
        ORDER BY b.updated_at ASC LIMIT :size OFFSET :pageNumber * :size
    """
    )
    fun getBookmarkListSortedUpdatedNewest(size: Int, pageNumber: Int): List<BookmarkAsListElement>

    @Query(
        """
        SELECT
            b.bookmarkId AS bookmarkId,
            n.ntt_id AS noticeId,
            n.notice_title AS noticeTitle,
            n.notice_category AS noticeCategory,
            b.isScheduled AS isReminderSet,
            b.created_at AS createdAt,
            b.updated_at AS updatedAt 
        FROM Bookmark b
        INNER JOIN NoticeEntity n ON n.ntt_id = b.target_ntt_id
        ORDER BY b.updated_at DESC LIMIT :size OFFSET :pageNumber * :size
    """
    )
    fun getBookmarkListSortedUpdatedOldest(size: Int, pageNumber: Int): List<BookmarkAsListElement>

    @Query(
        """
        SELECT
            b.bookmarkId AS bookmarkId,
            b.bookmark_note AS bookmarkNotes,
            n.notice_title AS noticeTitle
        FROM Bookmark b
        INNER JOIN NoticeEntity n ON n.ntt_id = b.target_ntt_id
    """
    )
    fun getFtsEntriesFromExistingTables(): List<BookmarkFtsTarget>

    @Query(
        """
        SELECT
            b.bookmarkId AS bookmarkId,
            n.ntt_id AS noticeId,
            n.notice_title AS noticeTitle,
            n.notice_category AS noticeCategory,
            b.isScheduled AS isReminderSet,
            b.created_at AS createdAt,
            b.updated_at AS updatedAt
        FROM BookmarkFts
        INNER JOIN Bookmark b ON b.bookmarkId = BookmarkFts.rowid
        INNER JOIN NoticeEntity n ON n.ntt_id = b.target_ntt_id
        WHERE BookmarkFts MATCH :keyword
        LIMIT :size OFFSET :pageNumber * :size
    """
    )
    fun getBookmarkListByKeywordFts(keyword: String, size: Int, pageNumber: Int): List<BookmarkAsListElement>

    @Query(
        """
        SELECT
            b.bookmarkId AS bookmarkId,
            n.ntt_id AS noticeId,
            n.notice_title AS noticeTitle,
            n.notice_category AS noticeCategory,
            b.isScheduled AS isReminderSet,
            b.created_at AS createdAt,
            b.updated_at AS updatedAt
        FROM Bookmark b
        INNER JOIN NoticeEntity n ON b.target_ntt_id = n.ntt_id
        WHERE b.bookmark_note LIKE '%' || :keyword || '%'
        LIMIT :size OFFSET :pageNumber * :size
    """
    )
    fun getBookmarkListByKeyword(keyword: String, size: Int, pageNumber: Int): List<BookmarkAsListElement>
}
