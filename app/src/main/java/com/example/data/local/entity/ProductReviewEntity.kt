package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_reviews")
data class ProductReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val customerName: String,
    val rating: Float,
    val reviewText: String,
    val date: String = "2026-07-28"
)
