package com.fmd2mobile.database.dao

import androidx.room.*
import com.fmd2mobile.database.entity.DownloadChunkEntity
import com.fmd2mobile.database.entity.DownloadEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for managing download queues, states, and chunk segment progress
 * for parallel chunked downloads.
 */
@Dao
interface DownloadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity)

    @Update
    suspend fun updateDownload(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteDownload(id: Long)

    @Query("SELECT * FROM downloads WHERE id = :id")
    suspend fun getDownloadById(id: Long): DownloadEntity?

    @Query("SELECT * FROM downloads")
    fun getAllDownloadsFlow(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE status = :status")
    suspend fun getDownloadsByStatus(status: String): List<DownloadEntity>

    // Chunk management
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChunks(chunks: List<DownloadChunkEntity>)

    @Query("UPDATE download_chunks SET bytesDownloaded = :bytesDownloaded WHERE downloadId = :downloadId AND chunkIndex = :chunkIndex")
    suspend fun updateChunkProgress(downloadId: Long, chunkIndex: Int, bytesDownloaded: Long)

    @Query("SELECT * FROM download_chunks WHERE downloadId = :downloadId")
    suspend fun getChunksForDownload(downloadId: Long): List<DownloadChunkEntity>

    @Query("DELETE FROM download_chunks WHERE downloadId = :downloadId")
    suspend fun deleteChunksForDownload(downloadId: Long)

    @Transaction
    suspend fun updateDownloadProgressAndSpeed(downloadId: Long, progress: Int, speed: Long, totalBytes: Long) {
        val download = getDownloadById(downloadId) ?: return
        updateDownload(
            download.copy(
                progress = progress,
                speed = speed,
                totalBytes = totalBytes
            )
        )
    }
}
