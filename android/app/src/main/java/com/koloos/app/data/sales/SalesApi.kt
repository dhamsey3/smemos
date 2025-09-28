package com.koloos.app.data.sales

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

@Serializable
data class SaleDto(
    val id: String,
    val total_amount_cents: Int,
    val status: String,
    val created_at: String
)

@Serializable
data class RecordSaleRequest(val items: List<SaleItemRequest>)

@Serializable
data class SaleItemRequest(val productId: String, val quantity: Int)

data class ApiResponse<T>(val data: T)

@Serializable
data class PaymentIntentRequest(val saleId: String, val channel: String)

@Serializable
data class PaymentIntentResponse(val data: PaymentIntentPayload)

@Serializable
data class PaymentIntentPayload(val reference: String, val qrImage: String?, val bankDetails: BankDetails?)

@Serializable
data class BankDetails(val accountNumber: String, val bankName: String)

@Serializable
data class ReceiptResponse(val data: ReceiptPayload)

@Serializable
data class ReceiptPayload(val message: String)

interface SalesApi {
    @GET("sales")
    suspend fun listSales(): ApiResponse<List<SaleDto>>

    @POST("sales")
    suspend fun recordSale(@Body body: RecordSaleRequest): ApiResponse<SaleDto>

    @POST("payments/initiate")
    suspend fun initiatePayment(@Body body: PaymentIntentRequest): PaymentIntentResponse

    @POST("receipts/{saleId}")
    suspend fun generateReceipt(@retrofit2.http.Path("saleId") saleId: String): ReceiptResponse
}
