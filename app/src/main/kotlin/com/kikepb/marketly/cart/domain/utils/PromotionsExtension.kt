package com.kikepb.marketly.cart.domain.utils

import com.kikepb.marketly.productlist.domain.model.PromotionModel
import java.time.Instant

fun List<PromotionModel>.activeAt(now: Instant): List<PromotionModel> = this.filter {
    it.startTime <= now && it.endTime >= now
}