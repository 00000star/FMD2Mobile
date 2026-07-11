package com.fmd2mobile.localsource.di

import android.content.Context
import com.fmd2mobile.core.repository.SettingsRepository
import com.fmd2mobile.localsource.MihonExporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * Hilt DI module providing MihonExporter singleton dependency.
 */
@Module
@InstallIn(SingletonComponent::class)
object LocalsourceModule {

    @Provides
    @Singleton
    fun provideMihonExporter(
        @ApplicationContext context: Context,
        settingsRepository: SettingsRepository,
        client: OkHttpClient
    ): MihonExporter {
        return MihonExporter(context, settingsRepository, client)
    }
}
