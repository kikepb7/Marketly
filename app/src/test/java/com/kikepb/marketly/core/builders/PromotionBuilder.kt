package com.kikepb.marketly.core.builders

import com.kikepb.marketly.productlist.domain.model.PromotionModel
import com.kikepb.marketly.productlist.domain.model.PromotionType
import com.kikepb.marketly.productlist.domain.model.PromotionType.PERCENT
import java.time.Instant

class PromotionBuilder {

    private var id = "promotion-1"
    private var type = PERCENT
    private var productIds = listOf("product-1")
    private var value = 10.0
    private var buyQuantity: Int? = null
    private var startTime = Instant.now().minusSeconds(3600)
    private var endTime = Instant.now().plusSeconds(3600)

    fun withId(id: String) = apply { this.id = id }
    fun withType(type: PromotionType) = apply { this.type = type }
    fun withProductIds(productIds: List<String>) = apply { this.productIds = productIds }
    fun withValue(value: Double) = apply { this.value = value }
    fun withBuyQuantity(buyQuantity: Int?) = apply { this.buyQuantity = buyQuantity }
    fun withStartTime(startTime: Instant) = apply { this.startTime = startTime }
    fun withEndTime(endTime: Instant) = apply { this.endTime = endTime }

    fun build() = PromotionModel(
        id = id,
        type = type,
        productIds = productIds,
        value = value,
        buyQuantity = buyQuantity,
        startTime = startTime,
        endTime = endTime
    )
}

fun promotion(block: PromotionBuilder.() -> Unit = {}) = PromotionBuilder().apply(block = block).build()