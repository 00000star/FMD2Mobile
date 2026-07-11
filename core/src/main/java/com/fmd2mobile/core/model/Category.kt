package com.fmd2mobile.core.model

/**
 * Domain model representing user-defined library categories.
 */
data class Category(
    val id: Long = 0,
    val name: String,
    val sortOrder: Int = 0
)
