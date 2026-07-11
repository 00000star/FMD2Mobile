package com.fmd2mobile.core.model

/**
 * Domain model representing reading history of chapters.
 */
data class History(
    val id: Long = 0,
    val mangaId: Long,
    val chapterId: Long,
    val lastReadTime: Long
)
