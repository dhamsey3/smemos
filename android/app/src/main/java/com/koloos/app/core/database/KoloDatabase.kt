package com.koloos.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.koloos.app.core.database.dao.PendingSyncDao
import com.koloos.app.core.database.dao.ProductDao
import com.koloos.app.core.database.dao.SaleDao
import com.koloos.app.core.database.entity.PendingSyncEntity
import com.koloos.app.core.database.entity.ProductEntity
import com.koloos.app.core.database.entity.SaleEntity

@Database(
    entities = [ProductEntity::class, SaleEntity::class, PendingSyncEntity::class],
    version = 1,
    exportSchema = false
)
abstract class KoloDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao
    abstract fun pendingSyncDao(): PendingSyncDao
}
