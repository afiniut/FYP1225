package com.example.hallbooking.models

data class Slot(
    val slotId: String = "",
    val hallName: String = "",
    val date: String = "", // Format: "2026-04-04" (YYYY-MM-DD)
    val dayName: String = "", // "Saturday" or "Sunday"
    val slotNumber: Int = 0, // 1, 2, or 3
    val startTime: String = "",
    val endTime: String = "",
    val isAvailable: Boolean = true,
    val isWholeDay: Boolean = false, // true for slot 3
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis()
)