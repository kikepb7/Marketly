package com.kikepb.marketly.di

import android.content.Context
import androidx.room.Room
import com.kikepb.marketly.core.domain.data.utils.DefaultDispatchersProvider
import com.kikepb.marketly.core.domain.utils.DispatchersProvider
import com.kikepb.marketly.productlist.data.local.database.MarketlyDatabase
import com.kikepb.marketly.productlist.data.repository.ProductRepositoryImpl
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDispatchersProvider(defaultDispatchersProvider: DefaultDispatchersProvider): DispatchersProvider = defaultDispatchersProvider

    @Provides
    @Singleton
    fun provideProductRepository(productRepositoryImpl: ProductRepositoryImpl): ProductRepository = productRepositoryImpl

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): MarketlyDatabase =
        Room.databaseBuilder(
            context = context,
            klass = MarketlyDatabase::class.java,
            name = "marketly_database"
        ).build()

    @Provides
    fun providesProductDao(database: MarketlyDatabase) = database.productDao()

    @Provides
    fun providesPromotionDao(database: MarketlyDatabase) = database.promotionDao()
}