package com.koloos.app.data.auth

import com.koloos.app.core.network.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val sessionManager: SessionManager
) {
    suspend fun requestOtp(phone: String): String = withContext(Dispatchers.IO) {
        api.requestOtp(RequestOtp(phone)).data.otp
    }

    suspend fun verifyOtp(phone: String, name: String, otp: String) = withContext(Dispatchers.IO) {
        val response = api.verifyOtp(VerifyOtpRequest(phone, name, otp)).data
        sessionManager.saveToken(response.tokens.accessToken)
        response.merchant
    }
}
