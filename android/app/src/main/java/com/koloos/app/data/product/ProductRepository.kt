package com.koloos.app.data.product

import com.koloos.app.core.database.dao.ProductDao
import com.koloos.app.core.database.entity.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val api: ProductApi,
    private val productDao: ProductDao
) {
    fun observeProducts(): Flow<List<ProductEntity>> = productDao.observeProducts()

    suspend fun refresh() = withContext(Dispatchers.IO) {
        val response = api.getProducts().data
        productDao.upsert(response.map { dto ->
            ProductEntity(
                id = dto.id,
                name = dto.name,
                priceCents = dto.price_cents,
                quantity = dto.quantity,
                updatedAt = System.currentTimeMillis()
            )
        })
    }

    suspend fun createProduct(name: String, price: Double, quantity: Int) = withContext(Dispatchers.IO) {
        val product = api.createProduct(CreateProductRequest(name, price, quantity)).data
        refresh()
        product
    }

    suspend fun updateProduct(id: String, name: String?, price: Double?, quantity: Int?) = withContext(Dispatchers.IO) {
        val product = api.updateProduct(id, UpdateProductRequest(name, price, quantity)).data
        refresh()
        product
    }

    suspend fun deleteProduct(id: String) = withContext(Dispatchers.IO) {
        api.deleteProduct(id)
        productDao.deleteById(id)
    }
}
