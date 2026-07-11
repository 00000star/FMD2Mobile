package com.fmd2mobile.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.fmd2mobile.core.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// DataStore delegate extension
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fmd2_settings")

/**
 * Implementation of SettingsRepository backing configurations on Android DataStore Preferences.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DOWNLOAD_LOCATION = stringPreferencesKey("download_location")
        val MAX_CONCURRENT_DOWNLOADS = intPreferencesKey("max_concurrent_downloads")
        val AMOLED_MODE = booleanPreferencesKey("amoled_mode")
    }

    override fun getThemeMode(): Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.THEME_MODE] ?: "SYSTEM"
    }

    override suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }

    override fun getDownloadLocation(): Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DOWNLOAD_LOCATION] ?: "/sdcard/Mihon/local"
    }

    override suspend fun setDownloadLocation(path: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DOWNLOAD_LOCATION] = path
        }
    }

    override fun getMaxConcurrentDownloads(): Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.MAX_CONCURRENT_DOWNLOADS] ?: 3
    }

    override suspend fun setMaxConcurrentDownloads(limit: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MAX_CONCURRENT_DOWNLOADS] = limit
        }
    }

    override fun isAmoledMode(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AMOLED_MODE] ?: false
    }

    override suspend fun setAmoledMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AMOLED_MODE] = enabled
        }
    }
}
