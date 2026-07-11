package com.fmd2mobile.database.repository

import com.fmd2mobile.core.model.Category
import com.fmd2mobile.core.model.Manga
import com.fmd2mobile.core.repository.MangaRepository
import com.fmd2mobile.database.dao.MangaDao
import com.fmd2mobile.database.entity.CategoryEntity
import com.fmd2mobile.database.entity.MangaCategoryCrossRef
import com.fmd2mobile.database.entity.MangaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MangaRepository that interfaces with Room Database MangaDao.
 */
@Singleton
class MangaRepositoryImpl @Inject constructor(
    private val mangaDao: MangaDao
) : MangaRepository {

    override fun getFavorites(): Flow<List<Manga>> {
        return mangaDao.getFavorites().map { list -> list.map { it.toDomain() } }
    }

    override fun getMangaById(id: Long): Flow<Manga?> {
        return mangaDao.getMangaById(id).map { it?.toDomain() }
    }

    override suspend fun getMangaByIdOneShot(id: Long): Manga? {
        return mangaDao.getMangaByIdOneShot(id)?.toDomain()
    }

    override suspend fun getMangaByUrl(url: String): Manga? {
        return mangaDao.getMangaByUrl(url)?.toDomain()
    }

    override suspend fun insertManga(manga: Manga): Long {
        return mangaDao.insertManga(MangaEntity.fromDomain(manga))
    }

    override suspend fun updateManga(manga: Manga) {
        mangaDao.updateManga(MangaEntity.fromDomain(manga))
    }

    override suspend fun deleteManga(manga: Manga) {
        mangaDao.deleteManga(MangaEntity.fromDomain(manga))
    }

    override fun searchMangas(query: String): Flow<List<Manga>> {
        return mangaDao.searchMangas(query).map { list -> list.map { it.toDomain() } }
    }

    // Categories
    override fun getAllCategories(): Flow<List<Category>> {
        return mangaDao.getAllCategories().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertCategory(category: Category): Long {
        return mangaDao.insertCategory(CategoryEntity.fromDomain(category))
    }

    override suspend fun deleteCategory(category: Category) {
        mangaDao.deleteCategory(CategoryEntity.fromDomain(category))
    }

    override suspend fun addMangaToCategory(mangaId: Long, categoryId: Long) {
        mangaDao.insertMangaCategoryCrossRef(MangaCategoryCrossRef(mangaId, categoryId))
    }

    override suspend fun removeMangaFromCategory(mangaId: Long, categoryId: Long) {
        mangaDao.deleteMangaCategoryCrossRef(mangaId, categoryId)
    }

    override fun getMangasByCategory(categoryId: Long): Flow<List<Manga>> {
        return mangaDao.getMangasByCategory(categoryId).map { list -> list.map { it.toDomain() } }
    }
}
