package com.kikepb.marketly.productlist.domain.usecase

import com.kikepb.marketly.productlist.domain.model.ProductWithPromotionModel
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import com.kikepb.marketly.productlist.domain.repository.PromotionRepository
import com.kikepb.marketly.productlist.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val getPromotionForProductUseCase: GetPromotionForProductUseCase,
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<List<ProductWithPromotionModel>> {
        return combine(
            flow = productRepository.getProducts(),
            flow2 = promotionRepository.getActivePromotions(),
            flow3 = settingsRepository.inStockOnly
        ) { products, promotions, inStockOnly ->
            val now = Instant.now()
            val activePromotions = promotions.filter { it.startTime <= now && it.endTime >= now }
            val filteredProducts = if (inStockOnly) products.filter { it.stock > 0 }  else products

            filteredProducts.map { product ->
                val promotion = getPromotionForProductUseCase(product, activePromotions)
                ProductWithPromotionModel(product = product, promotion = promotion)
            }
        }
    }
}