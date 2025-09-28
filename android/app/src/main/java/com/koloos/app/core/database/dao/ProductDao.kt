package com.koloos.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.koloos.app.core.database.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY updatedAt DESC")
    fun observeProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(products: List<ProductEntity>)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: String)
}
