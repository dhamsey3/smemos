package com.koloos.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey val id: String,
    val totalAmountCents: Int,
    val status: String,
    val createdAt: Long,
    val synced: Boolean
)
