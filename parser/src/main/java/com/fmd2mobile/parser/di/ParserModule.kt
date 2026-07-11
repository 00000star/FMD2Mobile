package com.fmd2mobile.parser.di

import com.fmd2mobile.core.source.MangaSource
import com.fmd2mobile.parser.source.MangaReaderSource
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt DI module providing network clients and binding manga sources.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ParserModule {

    @Binds
    @IntoMap
    @dagger.multibindings.StringKey("MangaReader")
    abstract fun bindMangaReaderSource(
        mangaReaderSource: MangaReaderSource
    ): MangaSource

    @Binds
    @IntoMap
    @dagger.multibindings.StringKey("MangaDex")
    abstract fun bindMangaDexSource(
        mangaDexSource: com.fmd2mobile.parser.source.mangadex.MangaDexSource
    ): MangaSource

    companion object {
        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            return OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build()
        }
    }
}
