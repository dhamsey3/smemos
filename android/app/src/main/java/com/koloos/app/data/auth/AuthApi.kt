package com.koloos.app.data.auth

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/request-otp")
    suspend fun requestOtp(@Body body: RequestOtp): ApiResponse<OtpResponse>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body body: VerifyOtpRequest): ApiResponse<AuthResponse>
}

data class RequestOtp(val phone: String)
data class OtpResponse(val data: OtpPayload)
data class OtpPayload(val otp: String)

data class VerifyOtpRequest(val phone: String, val name: String, val otp: String)
data class AuthResponse(val data: AuthPayload)
data class AuthPayload(val merchant: MerchantDto, val tokens: TokenDto)
data class MerchantDto(val id: String, val phone: String, val name: String)
data class TokenDto(val accessToken: String)

data class ApiResponse<T>(val data: T)
