package com.kikepb.marketly.productlist.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kikepb.marketly.productlist.data.local.database.dao.ProductDao
import com.kikepb.marketly.productlist.data.local.database.dao.PromotionDao
import com.kikepb.marketly.productlist.data.local.database.entity.ProductEntity
import com.kikepb.marketly.productlist.data.local.database.entity.PromotionEntity

@Database(
    entities = [ProductEntity::class, PromotionEntity::class],
    version = 1
)
abstract class MarketlyDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun promotionDao(): PromotionDao
}