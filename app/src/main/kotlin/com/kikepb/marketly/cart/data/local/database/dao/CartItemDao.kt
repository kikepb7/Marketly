package com.kikepb.marketly.cart.data.local.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.kikepb.marketly.cart.data.local.database.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

interface CartItemDao {

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun getCartItemById(productId: String): CartItemEntity?

    @Insert(onConflict = REPLACE)
    suspend fun insertCartItem(cartItemEntity: CartItemEntity)

    @Update
    suspend fun updateCartItem(cartItemEntity: CartItemEntity)

    @Delete
    suspend fun deleteCartItem(cartItemEntity: CartItemEntity)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}