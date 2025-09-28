package com.koloos.app.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KoloDatabase = Room.databaseBuilder(
        context,
        KoloDatabase::class.java,
        "koloos.db"
    ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideProductDao(db: KoloDatabase) = db.productDao()

    @Provides
    fun provideSaleDao(db: KoloDatabase) = db.saleDao()

    @Provides
    fun providePendingSyncDao(db: KoloDatabase) = db.pendingSyncDao()
}
