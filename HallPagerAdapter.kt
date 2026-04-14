package com.example.hallbooking.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hallbooking.R
import com.example.hallbooking.models.Hall

class HallPagerAdapter(private val hallList: List<Hall>) :
    RecyclerView.Adapter<HallPagerAdapter.HallViewHolder>() {

    class HallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val ivHallPicture: ImageView = itemView.findViewById(R.id.ivHallPicture)
        val tvHallName: TextView = itemView.findViewById(R.id.tvHallName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvCapacity: TextView = itemView.findViewById(R.id.tvCapacity)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HallViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_main, parent, false)
        return HallViewHolder(view)
    }

    override fun onBindViewHolder(holder: HallViewHolder, position: Int) {
        val hall = hallList[position]
        holder.tvHallName.text = hall.name
        holder.tvLocation.text = "📍 ${hall.location}"
        holder.tvCapacity.text = "👥 Capacity: ${hall.capacity} people"
        holder.tvPrice.text = "💰 RM ${hall.pricePerHour}/hour"
        holder.tvDescription.text = hall.description
    }

    override fun getItemCount() = hallList.size
}