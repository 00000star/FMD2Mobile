package com.fmd2mobile.database.dao

import androidx.room.*
import com.fmd2mobile.database.entity.ReadingProgressEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for persisting page reading updates in Room.
 */
@Dao
interface ReadingProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadingProgress(progress: ReadingProgressEntity)

    @Query("SELECT * FROM reading_progress WHERE chapterId = :chapterId")
    fun getReadingProgressFlow(chapterId: Long): Flow<ReadingProgressEntity?>

    @Query("SELECT * FROM reading_progress WHERE chapterId = :chapterId")
    suspend fun getReadingProgressOneShot(chapterId: Long): ReadingProgressEntity?
}
