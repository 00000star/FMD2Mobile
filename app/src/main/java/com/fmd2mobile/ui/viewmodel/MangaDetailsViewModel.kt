package com.fmd2mobile.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmd2mobile.core.model.Chapter
import com.fmd2mobile.core.model.Manga
import com.fmd2mobile.core.repository.ChapterRepository
import com.fmd2mobile.core.repository.MangaRepository
import com.fmd2mobile.downloader.DownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Manga Details Screen.
 * Provides streams for the manga profile, its chapters, and processes chapter download requests.
 */
@HiltViewModel
class MangaDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mangaRepository: MangaRepository,
    private val chapterRepository: ChapterRepository,
    private val downloadManager: DownloadManager
) : ViewModel() {

    private val mangaId: Long = savedStateHandle.get<Long>("mangaId") ?: 0L

    val manga: StateFlow<Manga?> = mangaRepository.getMangaById(mangaId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val chapters: StateFlow<List<Chapter>> = chapterRepository.getChaptersByMangaId(mangaId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * Enqueues a specific chapter to the download queue inside DownloadManager.
     */
    fun downloadChapter(chapter: Chapter) {
        val currentManga = manga.value ?: return
        viewModelScope.launch {
            downloadManager.enqueueDownload(currentManga, chapter)
        }
    }
}
