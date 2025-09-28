package com.koloos.app.data.sync

import com.koloos.app.core.database.dao.ProductDao
import com.koloos.app.core.database.entity.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val api: SyncApi,
    private val productDao: ProductDao
) {
    private val json = Json

    private var lastVersion: Long = 0L

    suspend fun pullLatest() = withContext(Dispatchers.IO) {
        val response = api.pull(SyncPullRequest(lastVersion)).data
        if (response.isEmpty()) return@withContext
        response.forEach { event ->
            when (event.entity_type) {
                "product" -> applyProduct(event)
            }
            lastVersion = maxOf(lastVersion, event.id)
        }
    }

    private suspend fun applyProduct(event: RemoteSyncEvent) {
        val payload = json.parseToJsonElement(event.payload).jsonObject
        if (payload["deleted"]?.jsonPrimitive?.booleanOrNull == true) {
            productDao.deleteById(event.entity_id)
        } else {
            val product = ProductEntity(
                id = event.entity_id,
                name = payload["name"]?.jsonPrimitive?.content ?: return,
                priceCents = payload["price_cents"]?.jsonPrimitive?.int ?: 0,
                quantity = payload["quantity"]?.jsonPrimitive?.int ?: 0,
                updatedAt = System.currentTimeMillis()
            )
            productDao.upsert(listOf(product))
        }
    }
}

private val kotlinx.serialization.json.JsonPrimitive.booleanOrNull: Boolean?
    get() = runCatching { boolean }.getOrNull()

private val kotlinx.serialization.json.JsonPrimitive.int: Int
    get() = intOrNull ?: 0

private val kotlinx.serialization.json.JsonPrimitive.intOrNull: Int?
    get() = runCatching { int }.getOrNull()
