package com.fmd2mobile.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fmd2mobile.core.model.Download
import com.fmd2mobile.core.model.DownloadStatus

/**
 * Room database Entity representing a Download job.
 * Linked to Manga and Chapter.
 */
@Entity(
    tableName = "downloads",
    foreignKeys = [
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["chapterId"])]
)
data class DownloadEntity(
    @PrimaryKey
    val id: Long, // Typically maps to the chapterId itself or auto-generated
    val mangaId: Long,
    val chapterId: Long,
    val status: String, // String representation of DownloadStatus
    val progress: Int,
    val speed: Long,
    val totalBytes: Long
) {
    fun toDomain(): Download = Download(
        id = id,
        mangaId = mangaId,
        chapterId = chapterId,
        status = DownloadStatus.valueOf(status),
        progress = progress,
        speed = speed,
        totalBytes = totalBytes
    )

    companion object {
        fun fromDomain(download: Download): DownloadEntity = DownloadEntity(
            id = download.id,
            mangaId = download.mangaId,
            chapterId = download.chapterId,
            status = download.status.name,
            progress = download.progress,
            speed = download.speed,
            totalBytes = download.totalBytes
        )
    }
}
