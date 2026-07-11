package com.fmd2mobile.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.fmd2mobile.core.model.DownloadChunk

/**
 * Room database Entity representing a segmented chunk of a download.
 * Used for resuming parallel chunked HTTP requests.
 */
@Entity(
    tableName = "download_chunks",
    primaryKeys = ["downloadId", "chunkIndex"],
    foreignKeys = [
        ForeignKey(
            entity = DownloadEntity::class,
            parentColumns = ["id"],
            childColumns = ["downloadId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["downloadId"])]
)
data class DownloadChunkEntity(
    val downloadId: Long,
    val chunkIndex: Int,
    val startByte: Long,
    val endByte: Long,
    val bytesDownloaded: Long
) {
    fun toDomain(): DownloadChunk = DownloadChunk(
        downloadId = downloadId,
        chunkIndex = chunkIndex,
        startByte = startByte,
        endByte = endByte,
        bytesDownloaded = bytesDownloaded
    )

    companion object {
        fun fromDomain(chunk: DownloadChunk): DownloadChunkEntity = DownloadChunkEntity(
            downloadId = chunk.downloadId,
            chunkIndex = chunk.chunkIndex,
            startByte = chunk.startByte,
            endByte = chunk.endByte,
            bytesDownloaded = chunk.bytesDownloaded
        )
    }
}
