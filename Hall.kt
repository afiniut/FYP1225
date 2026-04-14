package com.example.hallbooking.models

data class Hall(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val capacity: Int = 0,
    val pricePerHour: Double = 0.0,
    val imageUrl: String = "", // We'll use this for hall pictures
    val description: String = ""
)