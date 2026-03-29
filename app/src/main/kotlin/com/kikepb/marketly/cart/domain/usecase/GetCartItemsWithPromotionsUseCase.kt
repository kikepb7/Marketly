package com.kikepb.marketly.cart.domain.usecase

import com.kikepb.marketly.cart.domain.repository.CartRepository
import com.kikepb.marketly.cart.domain.utils.activeAt
import com.kikepb.marketly.cart.presentation.model.CartItemWithPromotionsUiModel
import com.kikepb.marketly.productlist.domain.model.ProductWithPromotionModel
import com.kikepb.marketly.productlist.domain.repository.ProductRepository
import com.kikepb.marketly.productlist.domain.repository.PromotionRepository
import com.kikepb.marketly.productlist.domain.usecase.GetPromotionForProductUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.Instant
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetCartItemsWithPromotionsUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val getPromotionForProductUseCase: GetPromotionForProductUseCase
) {

    operator fun invoke(): Flow<List<CartItemWithPromotionsUiModel>> =
        cartRepository.getCartItems().flatMapLatest { cartItems ->
            val ids = cartItems.mapTo(destination = mutableSetOf()) { it.productId }
            if (ids.isEmpty()) flowOf(value = emptyList())
            else {
                combine(
                    flow = productRepository.getProductsByIds(ids = ids),
                    flow2 = promotionRepository.getActivePromotions()
                ) { products, promotions ->
                    val now = Instant.now()
                    val activePromotions = promotions.activeAt(now)
                    val productsById = products.associateBy { it.id }

                    cartItems.mapNotNull { cartItem ->
                        val product = productsById[cartItem.productId] ?: return@mapNotNull null
                        val promotion = getPromotionForProductUseCase(product = product, promotions = activePromotions)
                        val productWithPromotion = ProductWithPromotionModel(product = product, promotion = promotion)

                        CartItemWithPromotionsUiModel(cartItem = cartItem, product = productWithPromotion)
                    }
                }
            }
        }
}