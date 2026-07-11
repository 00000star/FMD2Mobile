package com.fmd2mobile.database.repository

import com.fmd2mobile.core.model.Chapter
import com.fmd2mobile.core.repository.ChapterRepository
import com.fmd2mobile.database.dao.ChapterDao
import com.fmd2mobile.database.entity.ChapterEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ChapterRepository using ChapterDao database transactions.
 */
@Singleton
class ChapterRepositoryImpl @Inject constructor(
    private val chapterDao: ChapterDao
) : ChapterRepository {

    override suspend fun insertChapter(chapter: Chapter): Long {
        return chapterDao.insertChapter(ChapterEntity.fromDomain(chapter))
    }

    override suspend fun insertChapters(chapters: List<Chapter>) {
        chapterDao.insertChapters(chapters.map { ChapterEntity.fromDomain(it) })
    }

    override suspend fun updateChapter(chapter: Chapter) {
        chapterDao.updateChapter(ChapterEntity.fromDomain(chapter))
    }

    override suspend fun updateChapterStatusAndInfo(
        chapterId: Long,
        status: Chapter.Status,
        filePath: String,
        pageCount: Int
    ) {
        chapterDao.updateChapterStatusAndInfo(chapterId, status.name, filePath, pageCount)
    }

    override suspend fun updateChapterStatus(chapterId: Long, status: Chapter.Status) {
        chapterDao.updateChapterStatus(chapterId, status.name)
    }

    override suspend fun updateReadingProgress(chapterId: Long, isRead: Boolean, lastPageRead: Int, lastReadAt: Long) {
        chapterDao.updateReadingProgress(chapterId, isRead, lastPageRead, lastReadAt)
    }

    override fun getChapterById(chapterId: Long): Flow<Chapter?> {
        return chapterDao.getChapterById(chapterId).map { it?.toDomain() }
    }

    override suspend fun getChapterByIdOneShot(chapterId: Long): Chapter? {
        return chapterDao.getChapterByIdOneShot(chapterId)?.toDomain()
    }

    override fun getChaptersByMangaId(mangaId: Long): Flow<List<Chapter>> {
        return chapterDao.getChaptersByMangaId(mangaId).map { list -> list.map { it.toDomain() } }
    }
}
