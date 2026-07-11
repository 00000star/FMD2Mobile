package com.fmd2mobile.core.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface defining operations for managing application settings
 * and user preferences saved via DataStore.
 */
interface SettingsRepository {
    fun getThemeMode(): Flow<String>
    suspend fun setThemeMode(mode: String)
    fun getDownloadLocation(): Flow<String>
    suspend fun setDownloadLocation(path: String)
    fun getMaxConcurrentDownloads(): Flow<Int>
    suspend fun setMaxConcurrentDownloads(limit: Int)
    fun isAmoledMode(): Flow<Boolean>
    suspend fun setAmoledMode(enabled: Boolean)
}
