package com.fmd2mobile.core.model

/**
 * Domain model representing a Chapter of a Manga series.
 */
data class Chapter(
    val id: Long = 0,
    val mangaId: Long,
    val number: Float,
    val title: String,
    val url: String,
    val status: Status = Status.NOT_DOWNLOADED,
    val filePath: String = "",
    val pageCount: Int = 0
) {
    enum class Status {
        NOT_DOWNLOADED,
        DOWNLOADING,
        DOWNLOADED,
        FAILED
    }
}
