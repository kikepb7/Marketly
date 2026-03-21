package com.kikepb.marketly.productlist.domain.usecase

import com.kikepb.marketly.core.presentation.utils.roundToTwoDecimals
import com.kikepb.marketly.productlist.domain.model.ProductModel
import com.kikepb.marketly.productlist.domain.model.ProductPromotion
import com.kikepb.marketly.productlist.domain.model.PromotionModel
import com.kikepb.marketly.productlist.domain.model.PromotionType.BUY_X_PAY_Y
import com.kikepb.marketly.productlist.domain.model.PromotionType.PERCENT
import javax.inject.Inject

class GetPromotionForProductUseCase @Inject constructor() {
    operator fun invoke(product: ProductModel, promotions: List<PromotionModel>): ProductPromotion? {
        val productPromos = promotions.filter { it.productIds.contains(product.id) }

        val buyPayPromo = productPromos.firstOrNull { it.type == BUY_X_PAY_Y }
        if (buyPayPromo != null) {
            val buy = buyPayPromo.buyQuantity ?: return null
            val pay = buyPayPromo.value.toInt().coerceIn(0, buy)

            return ProductPromotion.BuyXPayY(
                buy = buy,
                pay = pay,
                label = "${buy}x${pay}"
            )
        }

        val percentPromo = productPromos.filter { it.type == PERCENT }
            .maxByOrNull { it.value }

        if (percentPromo != null) {
            val percent = percentPromo.value.coerceIn(0.0, 100.0)
            val discountedPrice = (product.price * (1 - percent / 100.0)).roundToTwoDecimals()
            return ProductPromotion.Percent(percent = percent, discountedPrice = discountedPrice)
        }

        return null
    }
}