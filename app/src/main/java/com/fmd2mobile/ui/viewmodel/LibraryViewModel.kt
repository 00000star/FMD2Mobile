package com.fmd2mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmd2mobile.core.model.Category
import com.fmd2mobile.core.model.Manga
import com.fmd2mobile.core.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Library Screen.
 * Provides data streams for favorite mangas, library categories, search query,
 * sorting, and category filters.
 */
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null) // null = "All"
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    private val _sortBy = MutableStateFlow(SortBy.ALPHABETICAL)
    val sortBy: StateFlow<SortBy> = _sortBy.asStateFlow()

    val categories: StateFlow<List<Category>> = mangaRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val libraryMangas: StateFlow<List<Manga>> = combine(
        _selectedCategoryId.flatMapLatest { catId ->
            if (catId == null) {
                mangaRepository.getFavorites()
            } else {
                mangaRepository.getMangasByCategory(catId)
            }
        },
        _searchQuery,
        _sortBy
    ) { mangas, query, sort ->
        var result = mangas

        // Search filtering
        if (query.isNotEmpty()) {
            result = result.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.author.contains(query, ignoreCase = true)
            }
        }

        // Sorting
        when (sort) {
            SortBy.ALPHABETICAL -> result.sortedBy { it.title }
            SortBy.LAST_UPDATED -> result.sortedByDescending { it.id } // Stub for updates order
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }

    fun setSortBy(sort: SortBy) {
        _sortBy.value = sort
    }

    fun toggleFavorite(manga: Manga) {
        viewModelScope.launch {
            mangaRepository.updateManga(manga.copy(isFavorite = !manga.isFavorite))
        }
    }

    enum class SortBy {
        ALPHABETICAL,
        LAST_UPDATED
    }
}
