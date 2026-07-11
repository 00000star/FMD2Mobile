package com.fmd2mobile.database.dao

import androidx.room.*
import com.fmd2mobile.database.entity.ChapterEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for accessing and modifying Chapter details in the Room database.
 */
@Dao
interface ChapterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Update
    suspend fun updateChapter(chapter: ChapterEntity)

    @Query("UPDATE chapters SET status = :status, filePath = :filePath, pageCount = :pageCount WHERE id = :chapterId")
    suspend fun updateChapterStatusAndInfo(chapterId: Long, status: String, filePath: String, pageCount: Int)

    @Query("UPDATE chapters SET status = :status WHERE id = :chapterId")
    suspend fun updateChapterStatus(chapterId: Long, status: String)

    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    fun getChapterById(chapterId: Long): Flow<ChapterEntity?>

    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    suspend fun getChapterByIdOneShot(chapterId: Long): ChapterEntity?

    @Query("SELECT * FROM chapters WHERE mangaId = :mangaId ORDER BY number DESC")
    fun getChaptersByMangaId(mangaId: Long): Flow<List<ChapterEntity>>
}
