package com.fmd2mobile.database.di

import com.fmd2mobile.core.repository.ChapterRepository
import com.fmd2mobile.core.repository.DownloadRepository
import com.fmd2mobile.core.repository.MangaRepository
import com.fmd2mobile.database.repository.ChapterRepositoryImpl
import com.fmd2mobile.database.repository.DownloadRepositoryImpl
import com.fmd2mobile.database.repository.MangaRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt DI module binding repository interfaces to their database-backed implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMangaRepository(
        mangaRepositoryImpl: MangaRepositoryImpl
    ): MangaRepository

    @Binds
    @Singleton
    abstract fun bindChapterRepository(
        chapterRepositoryImpl: ChapterRepositoryImpl
    ): ChapterRepository

    @Binds
    @Singleton
    abstract fun bindDownloadRepository(
        downloadRepositoryImpl: DownloadRepositoryImpl
    ): DownloadRepository
}
