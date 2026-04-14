package com.example.hallbooking.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hallbooking.R
import com.example.hallbooking.models.Hall

class HallAdapter(private val hallList: List<Hall>) :
    RecyclerView.Adapter<HallAdapter.HallViewHolder>() {

    class HallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvHallName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvHallLocation)
        val tvCapacity: TextView = itemView.findViewById(R.id.tvHallCapacity)
        val tvPrice: TextView = itemView.findViewById(R.id.tvHallPrice)
        val tvDescription: TextView = itemView.findViewById(R.id.tvHallDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HallViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hall, parent, false)
        return HallViewHolder(view)
    }

    override fun onBindViewHolder(holder: HallViewHolder, position: Int) {
        val hall = hallList[position]
        holder.tvName.text = hall.name
        holder.tvLocation.text = hall.location
        holder.tvCapacity.text = "Capacity: ${hall.capacity}"
        holder.tvPrice.text = "₹${hall.pricePerHour}/hr"
        holder.tvDescription.text = hall.description
    }

    override fun getItemCount() = hallList.size
}