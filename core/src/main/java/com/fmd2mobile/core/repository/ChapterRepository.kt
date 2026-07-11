package com.fmd2mobile.core.repository

import com.fmd2mobile.core.model.Chapter
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface defining operations for managing Manga Chapters.
 */
interface ChapterRepository {
    suspend fun insertChapter(chapter: Chapter): Long
    suspend fun insertChapters(chapters: List<Chapter>)
    suspend fun updateChapter(chapter: Chapter)
    suspend fun updateChapterStatusAndInfo(chapterId: Long, status: Chapter.Status, filePath: String, pageCount: Int)
    suspend fun updateChapterStatus(chapterId: Long, status: Chapter.Status)
    fun getChapterById(chapterId: Long): Flow<Chapter?>
    suspend fun getChapterByIdOneShot(chapterId: Long): Chapter?
    fun getChaptersByMangaId(mangaId: Long): Flow<List<Chapter>>
}
