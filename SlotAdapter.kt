// SlotAdapter.kt
package com.example.hallbooking.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hallbooking.R
import com.example.hallbooking.models.Slot

class SlotAdapter(private val slots: List<Slot>) :
    RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hallNameText: TextView = itemView.findViewById(R.id.slotHallNameText)
        val dateText: TextView = itemView.findViewById(R.id.slotDateText)
        val timeText: TextView = itemView.findViewById(R.id.slotTimeText)
        val statusText: TextView = itemView.findViewById(R.id.slotStatusText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slot, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slots[position]

        holder.hallNameText.text = slot.hallName
        holder.dateText.text = "Date: ${slot.date}"
        holder.timeText.text = "Time: ${slot.startTime} - ${slot.endTime}"
        holder.statusText.text = if (slot.isAvailable) "Available" else "Booked"

        // Set status color
        holder.statusText.setTextColor(
            if (slot.isAvailable)
                android.graphics.Color.GREEN
            else
                android.graphics.Color.RED
        )
    }

    override fun getItemCount() = slots.size
}