package com.koloos.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_sync")
data class PendingSyncEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val payload: String,
    val operation: String,
    val createdAt: Long
)
