package com.example.hallbooking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.hallbooking.models.Slot
import java.text.SimpleDateFormat
import java.util.*

class ConfirmationActivity : AppCompatActivity() {

    private lateinit var tvConfirmHallName: TextView
    private lateinit var tvConfirmSlot: TextView
    private lateinit var layoutAidsContainer: LinearLayout
    private lateinit var tvNoAids: TextView
    private lateinit var tvDepositStatus: TextView
    private lateinit var tvDepositAmount: TextView
    private lateinit var btnConfirmBooking: MaterialButton
    private lateinit var btnBackToHome: MaterialButton

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    // Store data from intent
    private var hallName: String = ""
    private var slotId: String = ""
    private var slotNumber: Int = 0
    private var date: String = ""
    private var dayName: String = ""
    private var startTime: String = ""
    private var endTime: String = ""
    private var isWholeDay: Boolean = false
    private var price: String = ""
    private var selectedAids: List<String> = listOf()
    private var totalDeposit: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Initialize views
        initializeViews()

        // Get data from intent
        getIntentData()

        // Display booking details
        displayBookingDetails()

        // Set up click listeners
        setupClickListeners()
    }

    private fun initializeViews() {
        tvConfirmHallName = findViewById(R.id.tvConfirmHallName)
        tvConfirmSlot = findViewById(R.id.tvConfirmSlot)
        layoutAidsContainer = findViewById(R.id.layoutAidsContainer)
        tvNoAids = findViewById(R.id.tvNoAids)
        tvDepositStatus = findViewById(R.id.tvDepositStatus)
        tvDepositAmount = findViewById(R.id.tvDepositAmount)
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking)
        btnBackToHome = findViewById(R.id.btnBackToHome)
    }

    private fun getIntentData() {
        hallName = intent.getStringExtra("HALL_NAME") ?: "Hall"
        slotId = intent.getStringExtra("SLOT_ID") ?: ""
        slotNumber = intent.getIntExtra("SLOT_NUMBER", 0)
        date = intent.getStringExtra("DATE") ?: ""
        dayName = intent.getStringExtra("DAY_NAME") ?: ""
        startTime = intent.getStringExtra("START_TIME") ?: ""
        endTime = intent.getStringExtra("END_TIME") ?: ""
        isWholeDay = intent.getBooleanExtra("IS_WHOLE_DAY", false)
        price = intent.getStringExtra("PRICE") ?: "RM 300.00"

        // Get selected aids - now receiving as Array of Strings
        selectedAids = intent.getStringArrayExtra("SELECTED_AIDS")?.toList() ?: listOf()
        totalDeposit = intent.getDoubleExtra("TOTAL_DEPOSIT", 0.0)

        // Debug log
        Log.d("Confirmation", "========== RECEIVED DATA ==========")
        Log.d("Confirmation", "Slot ID: $slotId")
        Log.d("Confirmation", "Hall: $hallName")
        Log.d("Confirmation", "Slot Number: $slotNumber")
        Log.d("Confirmation", "Date: $date")
        Log.d("Confirmation", "Selected aids: $selectedAids")
        Log.d("Confirmation", "Total deposit: $totalDeposit")
    }

    private fun displayBookingDetails() {
        tvConfirmHallName.text = hallName

        // Display slot details
        val slotDisplay = if (isWholeDay) {
            "Whole Day (8:00 AM - 7:00 PM) - $price"
        } else {
            "$startTime - $endTime - $price"
        }
        tvConfirmSlot.text = "$dayName, $date\n$slotDisplay"

        // Display aids
        if (selectedAids.isNotEmpty()) {
            layoutAidsContainer.visibility = View.VISIBLE
            tvNoAids.visibility = View.GONE
            layoutAidsContainer.removeAllViews()

            // Display each selected aid (quantity is always 1 now)
            selectedAids.forEach { aidName ->
                val aidRow = LinearLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 4, 0, 4)
                    }
                    orientation = LinearLayout.HORIZONTAL
                }

                val tvAidName = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    text = "• $aidName"
                    textSize = 14f
                }

                val tvAidQuantity = TextView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    text = "x1"
                    textSize = 14f
                    setTextColor(resources.getColor(android.R.color.darker_gray, null))
                }

                aidRow.addView(tvAidName)
                aidRow.addView(tvAidQuantity)
                layoutAidsContainer.addView(aidRow)
            }
        } else {
            layoutAidsContainer.visibility = View.GONE
            tvNoAids.visibility = View.VISIBLE
        }

        // Display deposit info
        val totalPrice = if (isWholeDay) 500.00 else 300.00
        val baseDeposit = totalPrice * 0.5

        tvDepositStatus.text = "⚠ Deposit Required (50%)"
        tvDepositStatus.setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))

        val finalDeposit = if (totalDeposit > 0) totalDeposit else baseDeposit
        tvDepositAmount.text = "RM ${String.format("%.2f", finalDeposit)}"
        tvDepositAmount.visibility = View.VISIBLE
    }

    private fun setupClickListeners() {
        btnConfirmBooking.setOnClickListener {
            confirmBooking()
        }

        btnBackToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun confirmBooking() {
        // Check if user is logged in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please login to continue", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Show loading indicator
        btnConfirmBooking.isEnabled = false
        btnConfirmBooking.text = "Processing..."

        // Step 1: Mark the selected slot as unavailable
        val slotRef = database.child("slots").child(slotId)

        Log.d("Confirmation", "🔨 Attempting to update slot: $slotId to unavailable")
        Log.d("Confirmation", "Slot reference path: slots/$slotId/isAvailable")

        slotRef.child("isAvailable").setValue(false)
            .addOnSuccessListener {
                Log.d("Confirmation", "✅✅✅ SLOT UPDATE SUCCESSFUL! Slot $slotId is now unavailable ✅✅✅")

                // Verify the update by reading it back
                slotRef.child("isAvailable").get().addOnSuccessListener { snapshot ->
                    val newValue = snapshot.getValue(Boolean::class.java)
                    Log.d("Confirmation", "Verified - Slot availability is now: $newValue")
                }

                // Step 2: If whole day booking, also mark Slot 1 and Slot 2 as unavailable
                if (isWholeDay) {
                    markSlotsAsUnavailableForWholeDay(date)
                }

                // Step 3: Save the booking record
                saveBookingToFirebase(currentUser.uid)

                // Step 4: Show success and navigate to receipt
                Toast.makeText(this, "Booking confirmed successfully!", Toast.LENGTH_LONG).show()

                // Convert List to HashMap for receipt (with quantity 1)
                val aidsMap = HashMap<String, Int>()
                selectedAids.forEach { aidsMap[it] = 1 }

                val intent = Intent(this, ReceiptActivity::class.java).apply {
                    putExtra("HALL_NAME", hallName)
                    putExtra("SLOT_NUMBER", slotNumber)
                    putExtra("DATE", date)
                    putExtra("DAY_NAME", dayName)
                    putExtra("START_TIME", startTime)
                    putExtra("END_TIME", endTime)
                    putExtra("IS_WHOLE_DAY", isWholeDay)
                    putExtra("PRICE", price)
                    putExtra("SELECTED_AIDS", aidsMap)
                    putExtra("TOTAL_DEPOSIT", totalDeposit)
                }
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                btnConfirmBooking.isEnabled = true
                btnConfirmBooking.text = "CONFIRM BOOKING"
                Log.e("Confirmation", "❌❌❌ SLOT UPDATE FAILED: ${e.message} ❌❌❌")
                Toast.makeText(this, "Failed to complete booking: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun markSlotsAsUnavailableForWholeDay(date: String) {
        val slotsRef = database.child("slots")

        // Find all slots for this date
        slotsRef.orderByChild("date").equalTo(date)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (slotSnapshot in snapshot.children) {
                        val slot = slotSnapshot.getValue(Slot::class.java)
                        // Mark Slot 1 and Slot 2 as unavailable
                        if (slot != null && slot.slotNumber in 1..2 && slot.isAvailable) {
                            slotSnapshot.ref.child("isAvailable").setValue(false)
                            Log.d("Confirmation", "Marked Slot ${slot.slotNumber} as unavailable due to whole day booking")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Confirmation", "Error updating slots: ${error.message}")
                }
            })
    }

    private fun saveBookingToFirebase(userId: String) {
        val bookingId = database.child("bookings").push().key ?: return

        val totalPrice = if (isWholeDay) 500.00 else 300.00
        val depositPaid = if (totalDeposit > 0) totalDeposit else totalPrice * 0.5
        val bookingDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Convert selected aids list to HashMap with quantity 1 for Firebase
        val aidsMap = HashMap<String, Int>()
        selectedAids.forEach { aidsMap[it] = 1 }

        val booking = mapOf(
            "bookingId" to bookingId,
            "userId" to userId,
            "userName" to (auth.currentUser?.displayName ?: "User"),
            "userPhone" to "",
            "hallName" to hallName,
            "slotId" to slotId,
            "slotNumber" to slotNumber,
            "date" to date,
            "dayName" to dayName,
            "startTime" to startTime,
            "endTime" to endTime,
            "isWholeDay" to isWholeDay,
            "totalPrice" to totalPrice,
            "depositPaid" to depositPaid,
            "depositStatus" to "pending",
            "bookingTimestamp" to System.currentTimeMillis(),
            "bookingDate" to bookingDate,
            "status" to "confirmed",
            "selectedAids" to aidsMap
        )

        database.child("bookings").child(bookingId).setValue(booking)
            .addOnSuccessListener {
                Log.d("Confirmation", "Booking saved successfully with ID: $bookingId")
            }
            .addOnFailureListener { e ->
                Log.e("Confirmation", "Error saving booking: ${e.message}")
            }
    }
}