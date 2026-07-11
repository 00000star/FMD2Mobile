package com.fmd2mobile.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fmd2mobile.database.dao.*
import com.fmd2mobile.database.entity.*

/**
 * Main Room Database for FMD2 Mobile.
 * Contains definitions for all tables, relations, and data access objects.
 */
@Database(
    entities = [
        MangaEntity::class,
        ChapterEntity::class,
        DownloadEntity::class,
        DownloadChunkEntity::class,
        CategoryEntity::class,
        MangaCategoryCrossRef::class,
        HistoryEntity::class,
        ReadingProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun chapterDao(): ChapterDao
    abstract fun downloadDao(): DownloadDao
    abstract fun historyDao(): HistoryDao
    abstract fun readingProgressDao(): ReadingProgressDao
}
