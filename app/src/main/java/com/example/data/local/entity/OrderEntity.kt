package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val shippingAddress: String,
    val paymentMethod: String, // Cash on Delivery, Bank Transfer, EasyPaisa, JazzCash, Card
    val totalAmount: Double,
    val discountAmount: Double = 0.0,
    val deliveryCharge: Double = 150.0,
    val finalAmount: Double,
    val status: String = "Pending", // Pending, Confirmed, Shipped, Delivered, Cancelled, Refunded
    val trackingNumber: String = "",
    val itemsSummary: String, // Summary of ordered products
    val createdAt: Long = System.currentTimeMillis()
)
