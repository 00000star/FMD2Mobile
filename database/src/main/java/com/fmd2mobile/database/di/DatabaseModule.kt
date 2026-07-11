package com.fmd2mobile.database.di

import android.content.Context
import androidx.room.Room
import com.fmd2mobile.database.AppDatabase
import com.fmd2mobile.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt Module providing database and DAO singleton dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fmd2_database"
        ).fallbackToDestructiveMigration() // Simple migration strategy for development
         .build()
    }

    @Provides
    @Singleton
    fun provideMangaDao(db: AppDatabase): MangaDao = db.mangaDao()

    @Provides
    @Singleton
    fun provideChapterDao(db: AppDatabase): ChapterDao = db.chapterDao()

    @Provides
    @Singleton
    fun provideDownloadDao(db: AppDatabase): DownloadDao = db.downloadDao()

    @Provides
    @Singleton
    fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historyDao()

    @Provides
    @Singleton
    fun provideReadingProgressDao(db: AppDatabase): ReadingProgressDao = db.readingProgressDao()
}
