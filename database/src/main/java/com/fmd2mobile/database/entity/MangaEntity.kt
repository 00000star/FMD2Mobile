package com.fmd2mobile.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fmd2mobile.core.model.Manga

/**
 * Room database Entity representing a Manga.
 */
@Entity(tableName = "mangas")
data class MangaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val artist: String,
    val description: String,
    val thumbnailUrl: String,
    val source: String,
    val url: String,
    val isFavorite: Boolean
) {
    fun toDomain(): Manga = Manga(
        id = id,
        title = title,
        author = author,
        artist = artist,
        description = description,
        thumbnailUrl = thumbnailUrl,
        source = source,
        url = url,
        isFavorite = isFavorite
    )

    companion object {
        fun fromDomain(manga: Manga): MangaEntity = MangaEntity(
            id = manga.id,
            title = manga.title,
            author = manga.author,
            artist = manga.artist,
            description = manga.description,
            thumbnailUrl = manga.thumbnailUrl,
            source = manga.source,
            url = manga.url,
            isFavorite = manga.isFavorite
        )
    }
}
