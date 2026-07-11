package com.fmd2mobile.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fmd2mobile.core.model.Category

/**
 * Room database Entity representing a library category.
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val sortOrder: Int
) {
    fun toDomain(): Category = Category(
        id = id,
        name = name,
        sortOrder = sortOrder
    )

    companion object {
        fun fromDomain(category: Category): CategoryEntity = CategoryEntity(
            id = category.id,
            name = category.name,
            sortOrder = category.sortOrder
        )
    }
}
