// AdminSlotsActivity.kt
package com.example.hallbooking

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hallbooking.R
import com.example.hallbooking.adapters.AdminSlotAdapter
import com.example.hallbooking.models.Slot
import com.example.hallbooking.models.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.hallbooking.LoginActivity
import android.content.Intent

class AdminSlotsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var bookingsDatabase: DatabaseReference
    private lateinit var slotsRecyclerView: RecyclerView
    private lateinit var adminSlotAdapter: AdminSlotAdapter
    private val slotsList = mutableListOf<Pair<Slot, Booking?>>() // Slot and its booking info (null if available)

    private lateinit var filterRadioGroup: RadioGroup
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_slots)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference.child("slots")
        bookingsDatabase = Firebase.database.reference.child("bookings")

        // Check if user is admin
        checkAdminStatus()

        // Initialize views
        initializeViews()

        // Setup RecyclerView
        setupRecyclerView()

        // Setup filter
        setupFilter()

        // Load slots
        loadSlots()

        // Setup logout button
        setupLogoutButton()
    }

    private fun checkAdminStatus() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            redirectToLogin()
            return
        }

        // Check if user is admin
        Firebase.database.reference.child("users").child(currentUser.uid)
            .child("userType").get()
            .addOnSuccessListener { snapshot ->
                val userType = snapshot.value.toString()
                if (userType != "admin") {
                    Toast.makeText(this, "Access denied. Admin only.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                redirectToLogin()
            }
    }

    private fun initializeViews() {
        slotsRecyclerView = findViewById(R.id.slotsRecyclerView)
        filterRadioGroup = findViewById(R.id.filterRadioGroup)
        logoutButton = findViewById(R.id.logoutButton)
    }

    private fun setupRecyclerView() {
        slotsRecyclerView.layoutManager = LinearLayoutManager(this)
        adminSlotAdapter = AdminSlotAdapter(slotsList)
        slotsRecyclerView.adapter = adminSlotAdapter
    }

    private fun setupFilter() {
        filterRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioAll -> adminSlotAdapter.filterSlots("all")
                R.id.radioAvailable -> adminSlotAdapter.filterSlots("available")
                R.id.radioBooked -> adminSlotAdapter.filterSlots("booked")
            }
        }
    }

    private fun loadSlots() {
        // First load all slots
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(slotsSnapshot: DataSnapshot) {
                slotsList.clear()

                // Then get all bookings to check which slots are booked
                bookingsDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(bookingsSnapshot: DataSnapshot) {

                        // Create a map of booked slots
                        val bookedSlots = mutableMapOf<String, Booking>()
                        for (bookingSnapshot in bookingsSnapshot.children) {
                            val booking = bookingSnapshot.getValue(Booking::class.java)
                            booking?.let {
                                // Assuming each slot can only be booked once
                                // You might need to adjust this based on your booking logic
                                bookedSlots[it.slotId] = it
                            }
                        }

                        // Now combine slots with their booking info
                        for (slotSnapshot in slotsSnapshot.children) {
                            val slot = slotSnapshot.getValue(Slot::class.java)
                            slot?.let {
                                val booking = bookedSlots[it.slotId]
                                slotsList.add(Pair(it, booking))
                            }
                        }

                        // Update adapter with all slots initially
                        adminSlotAdapter.updateList(slotsList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@AdminSlotsActivity,
                            "Error loading bookings: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminSlotsActivity,
                    "Error loading slots: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupLogoutButton() {
        logoutButton.setOnClickListener {
            auth.signOut()
            redirectToLogin()
        }
    }

    private fun redirectToLogin() {
        // Use the correct path to LoginActivity
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}