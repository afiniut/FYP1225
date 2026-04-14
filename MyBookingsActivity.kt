package com.example.hallbooking

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hallbooking.adapters.BookingAdapter
import com.example.hallbooking.models.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyBookingsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNoBookings: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val bookingsList = mutableListOf<Booking>()
    private lateinit var bookingAdapter: BookingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bookings)

        recyclerView = findViewById(R.id.recyclerViewBookings)
        tvNoBookings = findViewById(R.id.tvNoBookings)
        recyclerView.layoutManager = LinearLayoutManager(this)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        bookingAdapter = BookingAdapter(bookingsList)
        recyclerView.adapter = bookingAdapter

        loadBookings()
    }

    private fun loadBookings() {
        val userId = auth.currentUser?.uid ?: return

        database.child("bookings").orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    bookingsList.clear()

                    for (bookingSnapshot in snapshot.children) {
                        val booking = bookingSnapshot.getValue(Booking::class.java)
                        if (booking != null) {
                            bookingsList.add(booking)
                        }
                    }

                    if (bookingsList.isEmpty()) {
                        tvNoBookings.visibility = android.view.View.VISIBLE
                        recyclerView.visibility = android.view.View.GONE
                    } else {
                        tvNoBookings.visibility = android.view.View.GONE
                        recyclerView.visibility = android.view.View.VISIBLE
                        bookingAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
}