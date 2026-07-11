package com.fmd2mobile.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fmd2mobile.core.model.ReadingProgress

/**
 * Room database Entity representing user's page-level reading progress inside chapters.
 */
@Entity(
    tableName = "reading_progress",
    foreignKeys = [
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapterId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReadingProgressEntity(
    @PrimaryKey
    val chapterId: Long,
    val lastPageRead: Int,
    val totalPages: Int,
    val isCompleted: Boolean
) {
    fun toDomain(): ReadingProgress = ReadingProgress(
        chapterId = chapterId,
        lastPageRead = lastPageRead,
        totalPages = totalPages,
        isCompleted = isCompleted
    )

    companion object {
        fun fromDomain(progress: ReadingProgress): ReadingProgressEntity = ReadingProgressEntity(
            chapterId = progress.chapterId,
            lastPageRead = progress.lastPageRead,
            totalPages = progress.totalPages,
            isCompleted = progress.isCompleted
        )
    }
}
