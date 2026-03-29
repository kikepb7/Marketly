package com.kikepb.marketly.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kikepb.marketly.cart.data.local.database.dao.CartItemDao
import com.kikepb.marketly.cart.data.local.database.entity.CartItemEntity
import com.kikepb.marketly.productlist.data.local.database.dao.ProductDao
import com.kikepb.marketly.productlist.data.local.database.dao.PromotionDao
import com.kikepb.marketly.productlist.data.local.database.entity.ProductEntity
import com.kikepb.marketly.productlist.data.local.database.entity.PromotionEntity

@Database(
    entities = [ProductEntity::class, PromotionEntity::class, CartItemEntity::class],
    version = 1
)
abstract class MarketlyDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun promotionDao(): PromotionDao
    abstract fun cartItemDao(): CartItemDao
}