package com.kikepb.marketly.productlist.data.local

import com.kikepb.marketly.productlist.data.local.database.dao.ProductDao
import com.kikepb.marketly.productlist.data.local.database.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val promotionDao: ProductDao
) {

    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    suspend fun saveProducts(productsEntity: List<ProductEntity>) = productDao.replaceAll(products = productsEntity)
}