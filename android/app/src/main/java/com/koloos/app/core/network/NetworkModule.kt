package com.koloos.app.core.network

import com.koloos.app.data.auth.AuthApi
import com.koloos.app.data.product.ProductApi
import com.koloos.app.data.sales.SalesApi
import com.koloos.app.data.sync.SyncApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthApi(factory: ApiFactory): AuthApi = factory.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideProductApi(factory: ApiFactory): ProductApi = factory.create(ProductApi::class.java)

    @Provides
    @Singleton
    fun provideSalesApi(factory: ApiFactory): SalesApi = factory.create(SalesApi::class.java)

    @Provides
    @Singleton
    fun provideSyncApi(factory: ApiFactory): SyncApi = factory.create(SyncApi::class.java)
}
