package com.kikepb.marketly.productlist.domain.repository

import com.kikepb.marketly.productlist.domain.model.PromotionModel
import kotlinx.coroutines.flow.Flow

interface PromotionRepository {
    fun getActivePromotions(): Flow<List<PromotionModel>>
    suspend fun refreshPromotions()
}