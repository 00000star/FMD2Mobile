package com.fmd2mobile.database.repository

import com.fmd2mobile.core.model.Download
import com.fmd2mobile.core.model.DownloadChunk
import com.fmd2mobile.core.model.DownloadStatus
import com.fmd2mobile.core.repository.DownloadRepository
import com.fmd2mobile.database.dao.DownloadDao
import com.fmd2mobile.database.entity.DownloadChunkEntity
import com.fmd2mobile.database.entity.DownloadEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of DownloadRepository interfacing with Room DownloadDao.
 */
@Singleton
class DownloadRepositoryImpl @Inject constructor(
    private val downloadDao: DownloadDao
) : DownloadRepository {

    override suspend fun insertDownload(download: Download) {
        downloadDao.insertDownload(DownloadEntity.fromDomain(download))
    }

    override suspend fun updateDownload(download: Download) {
        downloadDao.updateDownload(DownloadEntity.fromDomain(download))
    }

    override suspend fun deleteDownload(id: Long) {
        downloadDao.deleteDownload(id)
    }

    override suspend fun getDownloadById(id: Long): Download? {
        return downloadDao.getDownloadById(id)?.toDomain()
    }

    override fun getAllDownloadsFlow(): Flow<List<Download>> {
        return downloadDao.getAllDownloadsFlow().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getDownloadsByStatus(status: DownloadStatus): List<Download> {
        return downloadDao.getDownloadsByStatus(status.name).map { it.toDomain() }
    }

    override suspend fun insertChunks(chunks: List<DownloadChunk>) {
        downloadDao.insertChunks(chunks.map { DownloadChunkEntity.fromDomain(it) })
    }

    override suspend fun updateChunkProgress(downloadId: Long, chunkIndex: Int, bytesDownloaded: Long) {
        downloadDao.updateChunkProgress(downloadId, chunkIndex, bytesDownloaded)
    }

    override suspend fun getChunksForDownload(downloadId: Long): List<DownloadChunk> {
        return downloadDao.getChunksForDownload(downloadId).map { it.toDomain() }
    }

    override suspend fun deleteChunksForDownload(downloadId: Long) {
        downloadDao.deleteChunksForDownload(downloadId)
    }

    override suspend fun updateDownloadProgressAndSpeed(
        downloadId: Long,
        progress: Int,
        speed: Long,
        totalBytes: Long
    ) {
        downloadDao.updateDownloadProgressAndSpeed(downloadId, progress, speed, totalBytes)
    }
}
