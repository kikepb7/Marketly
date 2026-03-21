package com.kikepb.marketly.productlist.domain.model

data class ProductWithPromotionModel(
    val product: ProductModel,
    val promotion: ProductPromotion?
)

sealed interface ProductPromotion {
    data class Percent(val percent: Double, val discountedPrice: Double): ProductPromotion
    data class BuyXPayY(val buy: Int, val pay: Int, val label: String): ProductPromotion
}