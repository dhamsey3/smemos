package com.koloos.app.data.sales

import com.koloos.app.core.database.dao.PendingSyncDao
import com.koloos.app.core.database.dao.SaleDao
import com.koloos.app.core.database.entity.PendingSyncEntity
import com.koloos.app.core.database.entity.SaleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SalesRepository @Inject constructor(
    private val api: SalesApi,
    private val saleDao: SaleDao,
    private val pendingSyncDao: PendingSyncDao
) {
    private val json = Json { encodeDefaults = true }

    fun observeSales(): Flow<List<SaleEntity>> = saleDao.observeSales()

    suspend fun recordSaleOffline(items: List<SaleItemRequest>) = withContext(Dispatchers.IO) {
        val saleId = UUID.randomUUID().toString()
        val sale = SaleEntity(
            id = saleId,
            totalAmountCents = items.sumOf { it.quantity * 100 },
            status = "PENDING",
            createdAt = System.currentTimeMillis(),
            synced = false
        )
        val pending = PendingSyncEntity(
            type = "sale",
            payload = json.encodeToString(RecordSaleRequest(items)),
            operation = "CREATE",
            createdAt = System.currentTimeMillis()
        )
        saleDao.insertSaleWithQueue(sale, pending)
    }

    suspend fun pushPendingSales() = withContext(Dispatchers.IO) {
        val queue = pendingSyncDao.loadPending()
        if (queue.isEmpty()) return@withContext
        // In a full implementation we would call sync API.
        pendingSyncDao.delete(queue.map { it.id })
    }
}
