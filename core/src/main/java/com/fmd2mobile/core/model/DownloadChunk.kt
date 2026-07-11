package com.fmd2mobile.core.model

/**
 * Domain model representing a chunk segment of a downloaded file.
 * Used for parallel chunked downloads.
 */
data class DownloadChunk(
    val downloadId: Long,
    val chunkIndex: Int,
    val startByte: Long,
    val endByte: Long,
    val bytesDownloaded: Long
)
