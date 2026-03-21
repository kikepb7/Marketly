package com.kikepb.marketly.productlist.data.local

import com.kikepb.marketly.productlist.data.local.database.dao.ProductDao
import com.kikepb.marketly.productlist.data.local.database.dao.PromotionDao
import com.kikepb.marketly.productlist.data.local.database.entity.ProductEntity
import com.kikepb.marketly.productlist.data.local.database.entity.PromotionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val promotionDao: PromotionDao
) {

    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    fun getAllPromotions(): Flow<List<PromotionEntity>> = promotionDao.getAllPromotions()

    suspend fun saveProducts(productsEntity: List<ProductEntity>) = productDao.replaceAll(products = productsEntity)

    suspend fun savePromotions(promotionsEntity: List<PromotionEntity>) = promotionDao.replaceAll(promotions = promotionsEntity)
}