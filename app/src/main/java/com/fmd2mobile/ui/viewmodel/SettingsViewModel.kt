package com.fmd2mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmd2mobile.core.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Settings Screen.
 * Provides user configuration preferences streams and functions to update them.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val themeMode: StateFlow<String> = settingsRepository.getThemeMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    val downloadLocation: StateFlow<String> = settingsRepository.getDownloadLocation()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "/sdcard/Mihon/local")

    val maxConcurrentDownloads: StateFlow<Int> = settingsRepository.getMaxConcurrentDownloads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 3)

    val isAmoledMode: StateFlow<Boolean> = settingsRepository.isAmoledMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setDownloadLocation(path: String) {
        viewModelScope.launch {
            settingsRepository.setDownloadLocation(path)
        }
    }

    fun setMaxConcurrentDownloads(limit: Int) {
        viewModelScope.launch {
            settingsRepository.setMaxConcurrentDownloads(limit)
        }
    }

    fun setAmoledMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAmoledMode(enabled)
        }
    }
}
