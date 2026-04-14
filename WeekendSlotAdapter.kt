package com.example.hallbooking.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.hallbooking.R
import com.example.hallbooking.models.Slot
import java.text.SimpleDateFormat
import java.util.*

class WeekendSlotAdapter(
    private var slots: List<Slot>,
    private val onSlotClick: (Slot) -> Unit
) : RecyclerView.Adapter<WeekendSlotAdapter.SlotViewHolder>() {

    private var selectedSlotId: String? = null

    class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardSlot: CardView = itemView.findViewById(R.id.cardSlot)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        val tvSlotNumber: TextView = itemView.findViewById(R.id.tvSlotNumber)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val dateDivider: View = itemView.findViewById(R.id.dateDivider)
        val tvDateHeader: TextView = itemView.findViewById(R.id.tvDateHeader)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weekend_slot, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slots[position]

        // Format date for display (e.g., "Saturday, 4 April 2026")
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateObj = formatter.parse(slot.date)
        val displayDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(dateObj!!)

        // Show date header if it's a new date
        if (position == 0 || slots[position - 1].date != slot.date) {
            holder.dateDivider.visibility = View.VISIBLE
            holder.tvDateHeader.visibility = View.VISIBLE
            holder.tvDateHeader.text = "${slot.dayName}, $displayDate"
        } else {
            holder.dateDivider.visibility = View.GONE
            holder.tvDateHeader.visibility = View.GONE
        }

        // Set slot details
        holder.tvDate.text = displayDate
        holder.tvDay.text = slot.dayName
        holder.tvSlotNumber.text = when (slot.slotNumber) {
            1 -> "Slot 1 (Morning)"
            2 -> "Slot 2 (Afternoon)"
            3 -> "Slot 3 (Whole Day)"
            else -> "Slot ${slot.slotNumber}"
        }
        holder.tvTime.text = "${slot.startTime} - ${slot.endTime}"
        holder.tvPrice.text = if (slot.slotNumber == 3) "RM 500" else "RM 300"

        // Set appearance based on availability
        if (!slot.isAvailable) {
            // Booked slot - greyed out
            holder.cardSlot.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.grey_200))
            holder.cardSlot.cardElevation = 2f
            holder.tvStatus.text = "BOOKED"
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            holder.itemView.isEnabled = false
        } else {
            // Available slot
            if (slot.slotId == selectedSlotId) {
                // Selected slot - use a different approach for selection highlight
                holder.cardSlot.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.blue_100))
                holder.cardSlot.cardElevation = 8f
                // Add a border effect using a background drawable
                holder.cardSlot.setContentPadding(4, 4, 4, 4)
                holder.tvStatus.text = "SELECTED"
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.blue_500))
            } else {
                // Regular available slot
                holder.cardSlot.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                holder.cardSlot.cardElevation = 4f
                holder.cardSlot.setContentPadding(0, 0, 0, 0)
                holder.tvStatus.text = "AVAILABLE"
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
            }
            holder.itemView.isEnabled = true
        }

        holder.itemView.setOnClickListener {
            if (slot.isAvailable) {
                selectedSlotId = slot.slotId
                onSlotClick(slot)
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount() = slots.size

    fun updateList(newList: List<Slot>) {
        slots = newList
        notifyDataSetChanged()
    }

    fun setSelectedSlot(slot: Slot?) {
        selectedSlotId = slot?.slotId
        notifyDataSetChanged()
    }
}