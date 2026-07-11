package com.fmd2mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmd2mobile.core.model.Manga
import com.fmd2mobile.core.repository.MangaRepository
import com.fmd2mobile.core.source.MangaSource
import com.fmd2mobile.core.source.SourceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Browse Screen.
 * Orchestrates calls to the MangaSource (HTML network parser) and handles saving
 * searched/popular mangas into local Room storage.
 */
@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val sourceManager: SourceManager,
    private val mangaRepository: MangaRepository
) : ViewModel() {

    // Default source is MangaDex if available, else whatever is first
    private val mangaSource: MangaSource = sourceManager.getSource("MangaDex") ?: sourceManager.getSources().values.first()

    private val _uiState = MutableStateFlow<BrowseUiState>(BrowseUiState.Idle)
    val uiState: StateFlow<BrowseUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentTab = MutableStateFlow(BrowseTab.POPULAR)
    val currentTab: StateFlow<BrowseTab> = _currentTab.asStateFlow()

    val sourceName: String = mangaSource.name

    init {
        loadTabContent(BrowseTab.POPULAR)
    }

    fun onTabSelected(tab: BrowseTab) {
        _currentTab.value = tab
        loadTabContent(tab)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun performSearch() {
        val query = _searchQuery.value
        if (query.isEmpty()) return

        _uiState.value = BrowseUiState.Loading
        viewModelScope.launch {
            try {
                val results = mangaSource.search(query, 1)
                _uiState.value = BrowseUiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = BrowseUiState.Error(e.message ?: "Failed to complete search")
            }
        }
    }

    private fun loadTabContent(tab: BrowseTab) {
        _uiState.value = BrowseUiState.Loading
        viewModelScope.launch {
            try {
                val results = when (tab) {
                    BrowseTab.POPULAR -> mangaSource.getPopular(1)
                    BrowseTab.LATEST -> mangaSource.getLatestUpdates(1)
                    BrowseTab.SEARCH -> emptyList()
                }
                _uiState.value = BrowseUiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = BrowseUiState.Error(e.message ?: "Failed to load content")
            }
        }
    }

    /**
     * Saves selected online manga into the database to persist local metadata.
     */
    fun addToLibrary(manga: Manga, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val existing = mangaRepository.getMangaByUrl(manga.url)
            val mangaId = if (existing != null) {
                if (!existing.isFavorite) {
                    mangaRepository.updateManga(existing.copy(isFavorite = true))
                }
                existing.id
            } else {
                // Fetch full details before adding to database
                val detailedManga = mangaSource.getMangaDetails(manga)
                mangaRepository.insertManga(detailedManga.copy(isFavorite = true))
            }
            onComplete(mangaId)
        }
    }

    sealed interface BrowseUiState {
        object Idle : BrowseUiState
        object Loading : BrowseUiState
        data class Success(val mangas: List<Manga>) : BrowseUiState
        data class Error(val message: String) : BrowseUiState
    }

    enum class BrowseTab {
        POPULAR,
        LATEST,
        SEARCH
    }
}
