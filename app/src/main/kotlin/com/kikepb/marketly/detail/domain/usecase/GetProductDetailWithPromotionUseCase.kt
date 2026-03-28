package com.kikepb.marketly.detail.domain.usecase

import com.kikepb.marketly.productlist.domain.model.ProductWithPromotionModel
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import com.kikepb.marketly.productlist.domain.repository.PromotionRepository
import com.kikepb.marketly.productlist.domain.usecase.GetPromotionForProductUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import javax.inject.Inject

class GetProductDetailWithPromotionUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val getPromotionForProductUseCase: GetPromotionForProductUseCase
) {

    operator fun invoke(productId: String): Flow<ProductWithPromotionModel?> =
        combine(
            flow = productRepository.getProductById(productId = productId),
            flow2 = promotionRepository.getActivePromotions()
        ) { product, promotions ->
            val now = Instant.now()
            val activePromotions = promotions.filter {
                it.startTime <= now && it.endTime >= now
            }

            product?.let {
                val finalPromotion = getPromotionForProductUseCase(product = it, promotions = activePromotions)
                ProductWithPromotionModel(product = it, promotion = finalPromotion)
            }
        }
}