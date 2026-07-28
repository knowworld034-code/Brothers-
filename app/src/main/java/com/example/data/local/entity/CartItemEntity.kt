package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: Long,
    val quantity: Int = 1,
    val selectedSize: String = "",
    val selectedColor: String = ""
)
