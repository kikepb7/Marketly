package com.kikepb.marketly.core.fakes

import com.kikepb.marketly.productlist.domain.model.PromotionModel
import com.kikepb.marketly.productlist.domain.repository.PromotionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePromotionRepository: PromotionRepository {

    private val _promotions = MutableStateFlow<List<PromotionModel>>(emptyList())

    fun setPromotions(promotions: List<PromotionModel>) {
        _promotions.value = promotions
    }
    override fun getActivePromotions(): Flow<List<PromotionModel>> = _promotions.asStateFlow()

    override suspend fun refreshPromotions() {}
}