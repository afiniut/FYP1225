package com.example.hallbooking

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hallbooking.adapters.BookingAdapter
import com.example.hallbooking.models.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var bookingsRecyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private val bookingsList = mutableListOf<Booking>()
    private lateinit var userId: String
    private lateinit var logoutButton: Button
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        // Initialize views
        toolbar = findViewById(R.id.toolbar)
        bookingsRecyclerView = findViewById(R.id.bookingsRecyclerView)
        logoutButton = findViewById(R.id.logoutButton)

        // Set up toolbar with back button
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "My Bookings"

        // Check if user is logged in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        userId = currentUser.uid

        // Setup RecyclerView
        setupRecyclerView()

        // Load user's bookings
        loadUserBookings()

        // Setup logout button
        setupLogoutButton()
    }

    // Handle back button click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Navigate back to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Handle system back button
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setupRecyclerView() {
        bookingsRecyclerView.layoutManager = LinearLayoutManager(this)
        bookingAdapter = BookingAdapter(bookingsList)
        bookingsRecyclerView.adapter = bookingAdapter
    }

    private fun loadUserBookings() {
        database.child("users").child(userId).get()
            .addOnSuccessListener { userSnapshot ->
                val userType = userSnapshot.child("userType").value.toString()

                if (userType == "admin") {
                    // Admin sees all bookings
                    database.child("bookings")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                bookingsList.clear()
                                for (bookingSnapshot in snapshot.children) {
                                    val booking = bookingSnapshot.getValue(Booking::class.java)
                                    booking?.let {
                                        bookingsList.add(it)
                                    }
                                }
                                bookingAdapter.notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@ProfileActivity,
                                    "Error loading bookings: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                } else {
                    // Customer sees only their bookings
                    database.child("bookings")
                        .orderByChild("userId")
                        .equalTo(userId)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                bookingsList.clear()
                                for (bookingSnapshot in snapshot.children) {
                                    val booking = bookingSnapshot.getValue(Booking::class.java)
                                    booking?.let {
                                        bookingsList.add(it)
                                    }
                                }
                                bookingAdapter.notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@ProfileActivity,
                                    "Error loading bookings: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get user type", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupLogoutButton() {
        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}