package com.kikepb.marketly.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.kikepb.marketly.cart.data.repository.CartRepositoryImpl
import com.kikepb.marketly.cart.domain.repository.CartRepository
import com.kikepb.marketly.core.domain.data.utils.DefaultDispatchersProvider
import com.kikepb.marketly.core.domain.utils.DispatchersProvider
import com.kikepb.marketly.core.data.local.database.MarketlyDatabase
import com.kikepb.marketly.productlist.data.repository.ProductRepositoryImpl
import com.kikepb.marketly.productlist.data.repository.PromotionRepositoryImpl
import com.kikepb.marketly.productlist.data.repository.SettingsRepositoryImpl
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import com.kikepb.marketly.productlist.domain.repository.PromotionRepository
import com.kikepb.marketly.productlist.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
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
    fun providePromotionRepository(promotionRepositoryImpl: PromotionRepositoryImpl): PromotionRepository = promotionRepositoryImpl

    @Provides
    @Singleton
    fun provideSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository = settingsRepositoryImpl

    @Provides
    @Singleton
    fun provideCartRepository(cartRepositoryImpl: CartRepositoryImpl): CartRepository = cartRepositoryImpl

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

    @Provides
    fun providesCartItemDao(database: MarketlyDatabase) = database.cartItemDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> = context.dataStore
}