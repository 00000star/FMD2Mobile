package com.fmd2mobile.core.repository

import com.fmd2mobile.core.model.Download
import com.fmd2mobile.core.model.DownloadChunk
import com.fmd2mobile.core.model.DownloadStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface defining operations for managing and tracking Downloads
 * and their respective segmented chunk progress.
 */
interface DownloadRepository {
    suspend fun insertDownload(download: Download)
    suspend fun updateDownload(download: Download)
    suspend fun deleteDownload(id: Long)
    suspend fun getDownloadById(id: Long): Download?
    fun getAllDownloadsFlow(): Flow<List<Download>>
    suspend fun getDownloadsByStatus(status: DownloadStatus): List<Download>
    
    // Chunk operations
    suspend fun insertChunks(chunks: List<DownloadChunk>)
    suspend fun updateChunkProgress(downloadId: Long, chunkIndex: Int, bytesDownloaded: Long)
    suspend fun getChunksForDownload(downloadId: Long): List<DownloadChunk>
    suspend fun deleteChunksForDownload(downloadId: Long)
    suspend fun updateDownloadProgressAndSpeed(downloadId: Long, progress: Int, speed: Long, totalBytes: Long)
}
