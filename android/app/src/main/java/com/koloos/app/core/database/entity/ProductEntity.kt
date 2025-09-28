package com.koloos.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val priceCents: Int,
    val quantity: Int,
    val updatedAt: Long
)
