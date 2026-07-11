package com.fmd2mobile.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fmd2mobile.core.model.Chapter

/**
 * Room database Entity representing a Chapter.
 * Has a foreign key linking to MangaEntity.
 */
@Entity(
    tableName = "chapters",
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["id"],
            childColumns = ["mangaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["mangaId"])]
)
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mangaId: Long,
    val number: Float,
    val title: String,
    val url: String,
    val status: String, // String representation of Chapter.Status
    val filePath: String,
    val pageCount: Int,
    val isRead: Boolean = false,
    val lastPageRead: Int = 0,
    val lastReadAt: Long = 0
) {
    fun toDomain(): Chapter = Chapter(
        id = id,
        mangaId = mangaId,
        number = number,
        title = title,
        url = url,
        status = Chapter.Status.valueOf(status),
        filePath = filePath,
        pageCount = pageCount,
        isRead = isRead,
        lastPageRead = lastPageRead,
        lastReadAt = lastReadAt
    )

    companion object {
        fun fromDomain(chapter: Chapter): ChapterEntity = ChapterEntity(
            id = chapter.id,
            mangaId = chapter.mangaId,
            number = chapter.number,
            title = chapter.title,
            url = chapter.url,
            status = chapter.status.name,
            filePath = chapter.filePath,
            pageCount = chapter.pageCount,
            isRead = chapter.isRead,
            lastPageRead = chapter.lastPageRead,
            lastReadAt = chapter.lastReadAt
        )
    }
}
