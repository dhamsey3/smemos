package com.koloos.app.data.product

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST

data class ProductDto(
    val id: String,
    val name: String,
    val price_cents: Int,
    val quantity: Int,
    val updated_at: String
)

data class CreateProductRequest(val name: String, val priceNaira: Double, val quantity: Int)

data class UpdateProductRequest(val name: String?, val priceNaira: Double?, val quantity: Int?)

data class ApiResponse<T>(val data: T)

interface ProductApi {
    @GET("products")
    suspend fun getProducts(): ApiResponse<List<ProductDto>>

    @POST("products")
    suspend fun createProduct(@Body body: CreateProductRequest): ApiResponse<ProductDto>

    @PATCH("products/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body body: UpdateProductRequest): ApiResponse<ProductDto>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: String)
}
