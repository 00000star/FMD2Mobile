package com.fmd2mobile.core.model

/**
 * Domain model representing a Manga or Comic book series.
 */
data class Manga(
    val id: Long = 0,
    val title: String,
    val author: String = "",
    val artist: String = "",
    val description: String = "",
    val thumbnailUrl: String = "",
    val source: String,
    val url: String,
    val isFavorite: Boolean = false
)
