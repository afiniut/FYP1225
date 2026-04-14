package com.example.hallbooking.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hallbooking.R
import com.example.hallbooking.models.Booking

class BookingAdapter(private val bookings: List<Booking>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hallNameText: TextView = itemView.findViewById(R.id.hallNameText)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val timeText: TextView = itemView.findViewById(R.id.timeText)
        val slotInfoText: TextView = itemView.findViewById(R.id.slotInfoText)  // 👈 Add this
        val statusText: TextView = itemView.findViewById(R.id.statusText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]

        holder.hallNameText.text = booking.hallName
        holder.dateText.text = "${booking.dayName}, ${booking.date}"

        // Show time and slot info
        if (booking.isWholeDay) {
            holder.timeText.text = "Whole Day (8:00 AM - 7:00 PM)"
            holder.slotInfoText.text = "Slot 3 (Whole Day) - RM 500.00"
        } else {
            holder.timeText.text = "${booking.startTime} - ${booking.endTime}"
            holder.slotInfoText.text = "Slot ${booking.slotNumber} - RM ${if (booking.slotNumber <= 2) "300.00" else "500.00"}"
        }

        // Show status with color
        holder.statusText.text = "Status: ${booking.status.uppercase()}"
        when (booking.status.lowercase()) {
            "confirmed" -> holder.statusText.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_green_dark)
            )
            "pending" -> holder.statusText.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_orange_dark)
            )
            "cancelled" -> holder.statusText.setTextColor(
                holder.itemView.context.getColor(android.R.color.holo_red_dark)
            )
            else -> holder.statusText.setTextColor(
                holder.itemView.context.getColor(android.R.color.darker_gray)
            )
        }
    }

    override fun getItemCount() = bookings.size
}