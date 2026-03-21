package com.kikepb.marketly.productlist.data.mapper

import com.kikepb.marketly.productlist.data.local.database.entity.PromotionEntity
import com.kikepb.marketly.productlist.data.remote.dto.PromotionResponseDto
import com.kikepb.marketly.productlist.domain.model.PromotionModel
import com.kikepb.marketly.productlist.domain.model.PromotionType
import com.kikepb.marketly.productlist.domain.model.PromotionType.BUY_X_PAY_Y
import com.kikepb.marketly.productlist.domain.model.PromotionType.PERCENT
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.time.Instant

fun PromotionResponseDto.toPromotionEntity(json: Json): PromotionEntity? {
    if (startAtEpoch == null || endAtEpoch == null) return null

    val productIds = listOf(productId)
    val productIdsJson = json.encodeToString(
        serializer = ListSerializer(String.serializer()),
        value = productIds
    )

    return PromotionEntity(
        id = id,
        productIds = productIdsJson,
        type = type,
        percent = percent,
        buyX = buyX,
        payY = payY,
        startAtEpoch = startAtEpoch,
        endAtEpoch = endAtEpoch
    )
}

fun PromotionEntity.toPromotionModel(json: Json): PromotionModel? {
    val decodeProductIds = runCatching { json.decodeFromString(
        deserializer = ListSerializer(String.serializer()),
        string = productIds
    ) }.getOrNull()

    val finalType = runCatching { PromotionType.valueOf(value = type.trim().uppercase()) }.getOrNull()

    if (finalType == null || decodeProductIds == null) return null

    val finalPromotionValue = when (finalType) {
        PERCENT -> percent
        BUY_X_PAY_Y -> payY
    }?.toDouble()

    finalPromotionValue ?: return null

    return PromotionModel(
        id = id,
        productIds = decodeProductIds,
        type = finalType,
        value = finalPromotionValue,
        buyQuantity = buyX,
        startTime = Instant.ofEpochSecond(startAtEpoch),
        endTime = Instant.ofEpochSecond(endAtEpoch)
    )
}