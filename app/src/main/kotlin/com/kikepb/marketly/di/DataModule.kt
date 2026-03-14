package com.kikepb.marketly.di

import com.kikepb.marketly.core.domain.data.utils.DefaultDispatchersProvider
import com.kikepb.marketly.core.domain.utils.DispatchersProvider
import com.kikepb.marketly.productlist.data.repository.ProductRepositoryImpl
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}