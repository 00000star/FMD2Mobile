package com.fmd2mobile.database.dao

import androidx.room.*
import com.fmd2mobile.database.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object representing user reading history transactions.
 */
@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Query("SELECT * FROM history ORDER BY lastReadTime DESC")
    fun getHistoryFlow(): Flow<List<HistoryEntity>>

    @Query("DELETE FROM history WHERE mangaId = :mangaId")
    suspend fun deleteHistoryByManga(mangaId: Long)

    @Query("DELETE FROM history")
    suspend fun clearHistory()
}
