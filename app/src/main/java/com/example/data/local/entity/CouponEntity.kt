package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey val code: String,
    val discountPercentage: Double,
    val maxDiscount: Double,
    val minOrderAmount: Double,
    val description: String,
    val isExpired: Boolean = false
)
