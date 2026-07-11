package com.fmd2mobile.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmd2mobile.core.model.Chapter
import com.fmd2mobile.core.repository.ChapterRepository
import com.fmd2mobile.core.source.MangaSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile
import javax.inject.Inject

/**
 * ViewModel for the Reader Screen.
 * Extracts images from local CBZ archives (offline) or fetches remote page URLs (online),
 * exposing a list of image sources to the Compose reader view.
 */
@HiltViewModel
class ReaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val chapterRepository: ChapterRepository,
    private val source: MangaSource
) : ViewModel() {

    private val chapterId: Long = savedStateHandle.get<Long>("chapterId") ?: 0L

    private val _uiState = MutableStateFlow<ReaderUiState>(ReaderUiState.Loading)
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    init {
        loadPages()
    }

    private fun loadPages() {
        viewModelScope.launch {
            _uiState.value = ReaderUiState.Loading
            try {
                val chapter = chapterRepository.getChapterByIdOneShot(chapterId)
                if (chapter == null) {
                    _uiState.value = ReaderUiState.Error("Chapter not found")
                    return@launch
                }

                if (chapter.status == Chapter.Status.DOWNLOADED && chapter.filePath.isNotEmpty()) {
                    // Offline mode: Unzip files from CBZ
                    val localPages = extractCbzPages(File(chapter.filePath), chapter.id)
                    if (localPages.isNotEmpty()) {
                        _uiState.value = ReaderUiState.Success(localPages, isOffline = true)
                    } else {
                        _uiState.value = ReaderUiState.Error("Failed to read CBZ page list")
                    }
                } else {
                    // Online mode: Fetch URLs
                    val remotePages = source.getPageList(chapter)
                    if (remotePages.isNotEmpty()) {
                        _uiState.value = ReaderUiState.Success(remotePages, isOffline = false)
                    } else {
                        _uiState.value = ReaderUiState.Error("Failed to fetch online pages")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ReaderUiState.Error(e.message ?: "Failed to load reader pages")
            }
        }
    }

    /**
     * Unpacks the CBZ zip file into a temporary cache folder and returns sorted absolute file paths.
     */
    private suspend fun extractCbzPages(cbzFile: File, chapterId: Long): List<String> = withContext(Dispatchers.IO) {
        val unpackDir = File(context.cacheDir, "reader/$chapterId").apply { mkdirs() }
        val pagePaths = mutableListOf<String>()

        try {
            ZipFile(cbzFile).use { zip ->
                val entries = zip.entries().asSequence().toList().sortedBy { it.name }
                entries.forEach { entry ->
                    if (!entry.isDirectory) {
                        val file = File(unpackDir, entry.name)
                        if (!file.exists()) {
                            zip.getInputStream(entry).use { input ->
                                FileOutputStream(file).use { output ->
                                    input.copyTo(output)
                                }
                            }
                        }
                        pagePaths.add(file.absolutePath)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        pagePaths
    }

    sealed interface ReaderUiState {
        object Loading : ReaderUiState
        data class Success(val pages: List<String>, val isOffline: Boolean) : ReaderUiState
        data class Error(val message: String) : ReaderUiState
    }
}
