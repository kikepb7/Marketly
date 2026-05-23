package com.kikepb.marketly.productlist.domain.usecase

import com.kikepb.marketly.cart.domain.utils.activeAt
import com.kikepb.marketly.core.domain.utils.ClockRepository
import com.kikepb.marketly.productlist.domain.model.ProductWithPromotionModel
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import com.kikepb.marketly.productlist.domain.repository.PromotionRepository
import com.kikepb.marketly.productlist.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val getPromotionForProductUseCase: GetPromotionForProductUseCase,
    private val settingsRepository: SettingsRepository,
    private val clockRepository: ClockRepository
) {
    operator fun invoke(): Flow<List<ProductWithPromotionModel>> {
        return combine(
            flow = productRepository.getProducts(),
            flow2 = promotionRepository.getActivePromotions(),
            flow3 = settingsRepository.inStockOnly
        ) { products, promotions, inStockOnly ->
            val now = clockRepository.now()
            val activePromotions = promotions.activeAt(now = now)
            val filteredProducts = if (inStockOnly) products.filter { it.stock > 0 }  else products

            filteredProducts.map { product ->
                val promotion = getPromotionForProductUseCase(product, activePromotions)
                ProductWithPromotionModel(product = product, promotion = promotion)
            }
        }
    }
}