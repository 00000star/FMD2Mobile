package com.fmd2mobile.core.source

import com.fmd2mobile.core.model.Chapter
import com.fmd2mobile.core.model.Manga

/**
 * Base Interface representing an online Manga Source.
 * Any new source can be added by implementing this interface.
 */
interface MangaSource {
    /**
     * Unique source name (e.g. "MangaDex")
     */
    val name: String

    /**
     * Base website URL of the source.
     */
    val baseUrl: String

    /**
     * Search for manga matching the given query and page.
     */
    suspend fun search(query: String, page: Int): List<Manga>

    /**
     * Get the latest updates from the source.
     */
    suspend fun getLatestUpdates(page: Int): List<Manga>

    /**
     * Get the popular mangas from the source.
     */
    suspend fun getPopular(page: Int): List<Manga>

    /**
     * Fetch complete details of the manga (author, description, thumbnail, etc.)
     */
    suspend fun getMangaDetails(manga: Manga): Manga

    /**
     * Get list of chapters for a given manga.
     */
    suspend fun getChapterList(manga: Manga): List<Chapter>

    /**
     * Get list of page image URLs for a chapter.
     */
    suspend fun getPageList(chapter: Chapter): List<String>
}
