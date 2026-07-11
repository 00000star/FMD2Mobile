package com.fmd2mobile.downloader.di

import android.content.Context
import com.fmd2mobile.core.repository.ChapterRepository
import com.fmd2mobile.core.repository.DownloadRepository
import com.fmd2mobile.downloader.DownloadManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt DI module providing DownloadManager singleton dependency.
 */
@Module
@InstallIn(SingletonComponent::class)
object DownloaderModule {

    @Provides
    @Singleton
    fun provideDownloadManager(
        @ApplicationContext context: Context,
        downloadRepository: DownloadRepository,
        chapterRepository: ChapterRepository
    ): DownloadManager {
        return DownloadManager(context, downloadRepository, chapterRepository)
    }
}
