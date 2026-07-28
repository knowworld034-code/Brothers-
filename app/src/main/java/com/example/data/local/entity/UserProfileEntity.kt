package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Customer User",
    val email: String = "Mrbast@gmail.com",
    val phone: String = "03472065158",
    val address: String = "Main Boulevard, Lahore, Pakistan",
    val city: String = "Lahore",
    val postalCode: String = "54000",
    val isLoggedIn: Boolean = true
)
