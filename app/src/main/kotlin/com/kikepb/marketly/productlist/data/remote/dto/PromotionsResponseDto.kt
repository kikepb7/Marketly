package com.kikepb.marketly.productlist.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PromotionsResponseDto(
    @SerialName("promotions")
    val promotions: List<PromotionResponseDto>
)

@Serializable
data class PromotionResponseDto(
    @SerialName("id")
    val id: String,
    @SerialName("productId")
    val productId: String,
    @SerialName("type")
    val type: String,
    @SerialName("percent")
    val percent: Int? = null,
    @SerialName("buyX")
    val buyX: Int? = null,
    @SerialName("payY")
    val payY: Int? = null,
    @SerialName("startAtEpoch")
    val startAtEpoch: Long? = null,
    @SerialName("endAtEpoch")
    val endAtEpoch: Long? = null,
)