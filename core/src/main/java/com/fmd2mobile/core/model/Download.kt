package com.fmd2mobile.core.model

/**
 * Domain model representing an active or queued Download job.
 */
data class Download(
    val id: Long = 0,
    val mangaId: Long,
    val chapterId: Long,
    val status: DownloadStatus = DownloadStatus.PENDING,
    val progress: Int = 0,
    val speed: Long = 0, // In bytes per second
    val totalBytes: Long = 0
)

enum class DownloadStatus {
    PENDING,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED
}
