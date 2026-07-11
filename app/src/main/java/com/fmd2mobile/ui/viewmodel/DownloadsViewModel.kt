package com.fmd2mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmd2mobile.core.model.Download
import com.fmd2mobile.downloader.DownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for Downloads Screen.
 * Exposes active background downlaod streams from DownloadManager and delegates controls.
 */
@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val downloadManager: DownloadManager
) : ViewModel() {

    val downloads: StateFlow<List<Download>> = downloadManager.downloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun pauseAll() {
        downloadManager.pauseAll()
    }

    fun resumeAll() {
        downloadManager.resumeAll()
    }

    fun cancelDownload(id: Long) {
        downloadManager.cancel(id)
    }
}
