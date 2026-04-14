package com.example.hallbooking.models

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val userType: String = "customer", // "customer" or "admin"
    val createdAt: Long = System.currentTimeMillis()
)