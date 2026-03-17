package com.kikepb.marketly.productlist.domain.model

data class PromotionModel(
    val id: String,
    val productId: String,
    val type: String,
    val percent: Int?,
    val buyX: Int?,
    val payY: Int?,
    val startAtEpoch: Long?,
    val endAtEpoch: Long?
)
