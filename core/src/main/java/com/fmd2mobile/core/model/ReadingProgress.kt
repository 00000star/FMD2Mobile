package com.fmd2mobile.core.model

/**
 * Domain model representing reading progress inside a specific chapter.
 */
data class ReadingProgress(
    val chapterId: Long,
    val lastPageRead: Int,
    val totalPages: Int,
    val isCompleted: Boolean = false
)
