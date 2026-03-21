package com.kikepb.marketly.productlist.domain.model

import java.time.Instant

data class PromotionModel(
    val id: String,
    val type: PromotionType,
    val productIds: List<String>,
    val value: Double,
    val buyQuantity: Int?,
    val startTime: Instant,
    val endTime: Instant
)

enum class PromotionType {
    PERCENT,
    BUY_X_PAY_Y
}