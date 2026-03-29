package com.kikepb.marketly.cart.domain.usecase

import com.kikepb.marketly.cart.domain.model.CartItemModel
import com.kikepb.marketly.cart.domain.model.CartSummaryModel
import com.kikepb.marketly.cart.domain.repository.CartRepository
import com.kikepb.marketly.cart.domain.utils.activeAt
import com.kikepb.marketly.productlist.domain.model.ProductModel
import com.kikepb.marketly.productlist.domain.model.ProductPromotion.BuyXPayY
import com.kikepb.marketly.productlist.domain.model.ProductPromotion.Percent
import com.kikepb.marketly.productlist.domain.model.PromotionModel
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

class GetCartSummaryUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val promotionRepository: PromotionRepository,
    private val getPromotionForProductUseCase: GetPromotionForProductUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<CartSummaryModel> = cartRepository.getCartItems()
        .flatMapLatest { cartItems ->
            val ids = cartItems.mapTo(destination = mutableSetOf()) { it.productId }
            if (ids.isEmpty()) {
                flowOf(
                    value = CartSummaryModel(
                        subtotal = 0.0,
                        totalDiscount = 0.0,
                        finalTotal = 0.0
                    )
                )
            } else {
                combine(
                    flow = productRepository.getProductsByIds(ids = ids),
                    flow2 = promotionRepository.getActivePromotions()
                ) { products, promotions ->
                    calculateSummary(
                        cartItems = cartItems,
                        products = products,
                        promotions = promotions
                    )
                }
            }
        }

    private fun calculateSummary(
        cartItems: List<CartItemModel>,
        products: List<ProductModel>,
        promotions: List<PromotionModel>
    ): CartSummaryModel {
        val now = Instant.now()
        val activePromotions = promotions.activeAt(now = now)

        val productsById = products.associateBy { it.id }
        var subtotal = 0.0
        var totalDiscount = 0.0

        for (cartItem in cartItems) {
            val product = productsById[cartItem.productId] ?: continue
            val totalItem = product.price * cartItem.quantity
            subtotal += totalItem

            totalDiscount += calculateDiscountByProduct(
                product = product,
                quantity = cartItem.quantity,
                activePromotions = activePromotions
            )
        }

        val total = (subtotal - totalDiscount).coerceAtLeast(minimumValue = 0.0)
        return CartSummaryModel(
            subtotal = subtotal,
            totalDiscount = totalDiscount,
            finalTotal = total
        )
    }

    private fun calculateDiscountByProduct(
        product: ProductModel,
        quantity: Int,
        activePromotions: List<PromotionModel>
    ): Double {
        val selectedPromotion = getPromotionForProductUseCase(product = product, promotions = activePromotions)

        return when (selectedPromotion) {
            is BuyXPayY -> {
                val buy = selectedPromotion.buy
                val pay = selectedPromotion.pay
                val freePerGroup = (buy - pay).coerceAtLeast(minimumValue = 0)
                val groups = quantity / buy
                val freeItems = freePerGroup * groups

                product.price * freeItems
            }
            is Percent -> {
                val itemSubTotal = product.price * quantity
                itemSubTotal * (selectedPromotion.percent / 100)
            }
            null -> 0.0
        }
    }
}