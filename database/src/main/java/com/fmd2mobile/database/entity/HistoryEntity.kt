package com.fmd2mobile.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fmd2mobile.core.model.History

/**
 * Room database Entity representing user's reading history.
 */
@Entity(
    tableName = "history",
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
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mangaId: Long,
    val chapterId: Long,
    val lastReadTime: Long
) {
    fun toDomain(): History = History(
        id = id,
        mangaId = mangaId,
        chapterId = chapterId,
        lastReadTime = lastReadTime
    )

    companion object {
        fun fromDomain(history: History): HistoryEntity = HistoryEntity(
            id = history.id,
            mangaId = history.mangaId,
            chapterId = history.chapterId,
            lastReadTime = history.lastReadTime
        )
    }
}
