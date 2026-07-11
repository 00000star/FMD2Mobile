package com.fmd2mobile.database.dao

import androidx.room.*
import com.fmd2mobile.database.entity.MangaEntity
import com.fmd2mobile.database.entity.CategoryEntity
import com.fmd2mobile.database.entity.MangaCategoryCrossRef
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for accessing and modifying Manga and Category data in the Room database.
 */
@Dao
interface MangaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManga(manga: MangaEntity): Long

    @Update
    suspend fun updateManga(manga: MangaEntity)

    @Delete
    suspend fun deleteManga(manga: MangaEntity)

    @Query("SELECT * FROM mangas WHERE id = :id")
    fun getMangaById(id: Long): Flow<MangaEntity?>

    @Query("SELECT * FROM mangas WHERE id = :id")
    suspend fun getMangaByIdOneShot(id: Long): MangaEntity?

    @Query("SELECT * FROM mangas WHERE url = :url LIMIT 1")
    suspend fun getMangaByUrl(url: String): MangaEntity?

    @Query("SELECT * FROM mangas")
    fun getAllMangas(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM mangas WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM mangas WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun searchMangas(query: String): Flow<List<MangaEntity>>

    // Categories
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories ORDER BY sortOrder ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    // Many-to-Many Category Linkage
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMangaCategoryCrossRef(ref: MangaCategoryCrossRef)

    @Query("DELETE FROM manga_category_cross_ref WHERE mangaId = :mangaId AND categoryId = :categoryId")
    suspend fun deleteMangaCategoryCrossRef(mangaId: Long, categoryId: Long)

    @Transaction
    @Query("SELECT * FROM mangas WHERE id IN (SELECT mangaId FROM manga_category_cross_ref WHERE categoryId = :categoryId)")
    fun getMangasByCategory(categoryId: Long): Flow<List<MangaEntity>>
}
