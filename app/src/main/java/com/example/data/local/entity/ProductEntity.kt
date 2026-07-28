package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // Shoes, Slippers, Clothing, Electronics, Grocery, Digital
    val brand: String,
    val imageUrl: String,
    val videoUrl: String = "",
    val description: String,
    val shortDescription: String,
    val price: Double,
    val discountPrice: Double? = null,
    val stockQuantity: Int,
    val sku: String,
    val sizes: String = "M, L, XL", // Comma separated sizes
    val colors: String = "Black, Brown, Navy", // Comma separated colors
    val weight: String = "0.5 kg",
    val specifications: String = "",
    val tags: String = "Featured, BestSeller",
    val deliveryCharges: Double = 150.0,
    val warranty: String = "6 Months Official Warranty",
    val returnPolicy: String = "7 Days Easy Replacement",
    val isPublished: Boolean = true,
    val isFeatured: Boolean = false,
    val rating: Float = 4.8f,
    val reviewCount: Int = 12,
    val viewCount: Int = 150,
    val searchCount: Int = 45,
    val wishlistCount: Int = 20
)
