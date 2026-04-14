// AdminSlotAdapter.kt
package com.example.hallbooking.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hallbooking.R
import com.example.hallbooking.models.Slot
import com.example.hallbooking.models.Booking

class AdminSlotAdapter(private var slots: List<Pair<Slot, Booking?>>) :
    RecyclerView.Adapter<AdminSlotAdapter.SlotViewHolder>() {

    private var filteredSlots: List<Pair<Slot, Booking?>> = slots
    private var filterType: String = "all"

    class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hallNameText: TextView = itemView.findViewById(R.id.slotHallNameText)
        val dateText: TextView = itemView.findViewById(R.id.slotDateText)
        val timeText: TextView = itemView.findViewById(R.id.slotTimeText)
        val statusText: TextView = itemView.findViewById(R.id.slotStatusText)
        val bookedByText: TextView = itemView.findViewById(R.id.bookedByText)
        val bookingDetailsText: TextView = itemView.findViewById(R.id.bookingDetailsText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_slot, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val (slot, booking) = filteredSlots[position]
        val isBooked = booking != null

        holder.hallNameText.text = slot.hallName
        holder.dateText.text = "Date: ${slot.date}"
        holder.timeText.text = "Time: ${slot.startTime} - ${slot.endTime}"

        if (isBooked) {
            holder.statusText.text = "Booked"
            holder.statusText.setTextColor(android.graphics.Color.RED)

            // Show booking details
            holder.bookedByText.visibility = View.VISIBLE
            holder.bookingDetailsText.visibility = View.VISIBLE

            holder.bookedByText.text = "Booked by: ${booking?.userName ?: "Unknown"}"
            holder.bookingDetailsText.text = "Contact: ${booking?.userPhone ?: "N/A"}"
        } else {
            holder.statusText.text = "Available"
            holder.statusText.setTextColor(android.graphics.Color.GREEN)

            // Hide booking details
            holder.bookedByText.visibility = View.GONE
            holder.bookingDetailsText.visibility = View.GONE
        }
    }

    override fun getItemCount() = filteredSlots.size

    fun filterSlots(type: String) {
        filterType = type
        filteredSlots = when (type) {
            "available" -> slots.filter { it.second == null }
            "booked" -> slots.filter { it.second != null }
            else -> slots
        }
        notifyDataSetChanged()
    }

    fun updateList(newList: List<Pair<Slot, Booking?>>) {
        slots = newList
        filterSlots(filterType) // Re-apply current filter
    }
}