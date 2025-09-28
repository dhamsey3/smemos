package com.koloos.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.koloos.app.core.database.entity.PendingSyncEntity

@Dao
interface PendingSyncDao {
    @Query("SELECT * FROM pending_sync ORDER BY createdAt ASC LIMIT :limit")
    suspend fun loadPending(limit: Int = 20): List<PendingSyncEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PendingSyncEntity): Long

    @Query("DELETE FROM pending_sync WHERE id IN (:ids)")
    suspend fun delete(ids: List<Long>)
}
