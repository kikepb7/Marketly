package com.kikepb.marketly.productlist.domain.usecase

import com.kikepb.marketly.productlist.domain.model.ProductWithPromotionModel
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import com.kikepb.marketly.productlist.domain.repository.PromotionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val getPromotionForProductUseCase: GetPromotionForProductUseCase
) {
    operator fun invoke(): Flow<List<ProductWithPromotionModel>> {
        return combine(
            flow = productRepository.getProducts(),
            flow2 = promotionRepository.getActivePromotions()
        ) { products, promotions ->
            val now = Instant.now()
            val activePromotions = promotions.filter { it.startTime <= now && it.endTime >= now }

            products.map { product ->
                val promotion = getPromotionForProductUseCase(product, activePromotions)
                ProductWithPromotionModel(product = product, promotion = promotion)
            }
        }
    }
}