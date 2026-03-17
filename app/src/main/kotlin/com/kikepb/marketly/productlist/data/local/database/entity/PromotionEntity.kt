package com.kikepb.marketly.productlist.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "promotions")
data class PromotionEntity(
    @PrimaryKey
    val id: String,
    val productId: String,
    val type: String,
    val percent: Int?,
    val buyX: Int?,
    val payY: Int?,
    val startAtEpoch: Long?,
    val endAtEpoch: Long?
)