package com.koloos.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.koloos.app.core.database.entity.PendingSyncEntity
import com.koloos.app.core.database.entity.SaleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY createdAt DESC")
    fun observeSales(): Flow<List<SaleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSale(sale: SaleEntity)

    @Insert
    suspend fun insertPending(entity: PendingSyncEntity)

    @Transaction
    suspend fun insertSaleWithQueue(sale: SaleEntity, pending: PendingSyncEntity) {
        upsertSale(sale)
        insertPending(pending)
    }
}
