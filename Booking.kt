package com.example.hallbooking.models

data class Booking(
    val bookingId: String = "",
    val slotId: String = "",
    val slotNumber: Int = 0,  // 👈 Add this (1, 2, or 3)
    val userId: String = "",
    val userName: String = "",
    val userPhone: String = "",
    val hallName: String = "",
    val date: String = "",
    val dayName: String = "",  // 👈 Add this (Saturday, Sunday)
    val startTime: String = "",
    val endTime: String = "",
    val isWholeDay: Boolean = false,  // 👈 Add this
    val totalPrice: Double = 0.0,  // 👈 Add this
    val depositPaid: Double = 0.0,  // 👈 Add this
    val depositStatus: String = "pending",  // 👈 Add this
    val status: String = "confirmed",
    val bookingTimestamp: Long = System.currentTimeMillis()
)