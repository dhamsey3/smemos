package com.koloos.app.core.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

private const val BASE_URL = "https://api.koloos.local/api/v1/"

@Singleton
class ApiFactory @Inject constructor(
    private val tokenProvider: TokenProvider
) {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val authInterceptor = Interceptor { chain ->
        val builder = chain.request().newBuilder()
        tokenProvider.token?.let { builder.addHeader("Authorization", "Bearer $it") }
        chain.proceed(builder.build())
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()

    fun <T> create(service: Class<T>): T = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(service)
}

interface TokenProvider {
    val token: String?
}
