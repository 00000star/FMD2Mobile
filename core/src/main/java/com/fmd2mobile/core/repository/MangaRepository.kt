package com.fmd2mobile.core.repository

import com.fmd2mobile.core.model.Manga
import com.fmd2mobile.core.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface defining operations for managing Manga, Categories, and Favorites.
 */
interface MangaRepository {
    fun getFavorites(): Flow<List<Manga>>
    fun getMangaById(id: Long): Flow<Manga?>
    suspend fun getMangaByIdOneShot(id: Long): Manga?
    suspend fun getMangaByUrl(url: String): Manga?
    suspend fun insertManga(manga: Manga): Long
    suspend fun updateManga(manga: Manga)
    suspend fun deleteManga(manga: Manga)
    fun searchMangas(query: String): Flow<List<Manga>>
    
    // Categories
    fun getAllCategories(): Flow<List<Category>>
    suspend fun insertCategory(category: Category): Long
    suspend fun deleteCategory(category: Category)
    suspend fun addMangaToCategory(mangaId: Long, categoryId: Long)
    suspend fun removeMangaFromCategory(mangaId: Long, categoryId: Long)
    fun getMangasByCategory(categoryId: Long): Flow<List<Manga>>
}
