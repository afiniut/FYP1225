package com.example.hallbooking

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hallbooking.adapters.WeekendSlotAdapter
import com.example.hallbooking.models.Slot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class BookingPageActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var slotsRecyclerView: RecyclerView
    private lateinit var slotAdapter: WeekendSlotAdapter
    private val allSlotsMap = mutableMapOf<String, Slot>()
    private lateinit var tvHallName: TextView
    private lateinit var tvMonthHeader: TextView
    private lateinit var btnProceed: Button
    private lateinit var btnPrevMonth: Button
    private lateinit var btnNextMonth: Button
    private lateinit var btnTestCreate: Button
    private lateinit var btnTestUpdate: Button
    private var selectedSlot: Slot? = null
    private var hallName: String = ""
    private var currentMonthIndex = 0
    private val months = listOf("April 2026", "May 2026")
    private var currentDisplayList = mutableListOf<Slot>()

    private val weekendDates = listOf(
        "2026-04-04", "2026-04-05", "2026-04-11", "2026-04-12",
        "2026-04-18", "2026-04-19", "2026-04-25", "2026-04-26",
        "2026-05-02", "2026-05-03", "2026-05-09", "2026-05-10",
        "2026-05-16", "2026-05-17", "2026-05-23", "2026-05-24",
        "2026-05-30", "2026-05-31"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_page)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        hallName = intent.getStringExtra("HALL_NAME") ?: "Dewan Seri Utama"

        initViews()
        setupRecyclerView()
        setupButtons()
        loadSlotsFromFirebase()
    }

    private fun initViews() {
        tvHallName = findViewById(R.id.tvHallName)
        tvMonthHeader = findViewById(R.id.tvMonthHeader)
        slotsRecyclerView = findViewById(R.id.slotsRecyclerView)
        btnProceed = findViewById(R.id.btnProceed)
        btnPrevMonth = findViewById(R.id.btnPrevMonth)
        btnNextMonth = findViewById(R.id.btnNextMonth)
        btnTestCreate = findViewById(R.id.btnTestCreate)
        btnTestUpdate = findViewById(R.id.btnTestUpdate)

        tvHallName.text = hallName
        tvMonthHeader.text = months[0]
    }

    private fun setupRecyclerView() {
        slotsRecyclerView.layoutManager = LinearLayoutManager(this)
        slotAdapter = WeekendSlotAdapter(currentDisplayList) { slot ->
            handleSlotSelection(slot)
        }
        slotsRecyclerView.adapter = slotAdapter
    }

    private fun setupButtons() {
        btnPrevMonth.setOnClickListener {
            if (currentMonthIndex > 0) {
                currentMonthIndex--
                tvMonthHeader.text = months[currentMonthIndex]
                updateDisplay()
            }
        }

        btnNextMonth.setOnClickListener {
            if (currentMonthIndex < months.size - 1) {
                currentMonthIndex++
                tvMonthHeader.text = months[currentMonthIndex]
                updateDisplay()
            }
        }

        btnTestCreate.setOnClickListener {
            createAllSlotsManually()
        }

        btnTestUpdate.setOnClickListener {
            testUpdateSlot()
        }

        btnProceed.setOnClickListener {
            proceedToAidSelection()
        }
    }

    private fun loadSlotsFromFirebase() {
        Log.d("BookingPage", "========== LOADING SLOTS FROM FIREBASE ==========")

        database.child("slots").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("BookingPage", "🔥🔥🔥 DATA CHANGED! Firebase snapshot received 🔥🔥🔥")
                Log.d("BookingPage", "Total slots in Firebase: ${snapshot.childrenCount}")

                // Clear and reload all slots
                allSlotsMap.clear()

                for (slotSnapshot in snapshot.children) {
                    val slot = slotSnapshot.getValue(Slot::class.java)
                    slot?.let {
                        if (it.hallName == hallName) {
                            allSlotsMap[it.slotId] = it
                            Log.d("BookingPage", "Loaded: ${it.date} - Slot ${it.slotNumber} - Available: ${it.isAvailable}")
                        }
                    }
                }

                Log.d("BookingPage", "Total slots loaded for $hallName: ${allSlotsMap.size}")

                // Force update the display
                runOnUiThread {
                    updateDisplay()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BookingPage", "Error loading slots: ${error.message}")
                Toast.makeText(this@BookingPageActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateDisplay() {
        val yearMonth = if (currentMonthIndex == 0) "2026-04" else "2026-05"
        Log.d("BookingPage", "Updating display for month: $yearMonth")

        // Filter slots for current month and sort
        val monthSlots = allSlotsMap.values
            .filter { it.date.startsWith(yearMonth) }
            .sortedWith(compareBy({ it.date }, { it.slotNumber }))

        Log.d("BookingPage", "Found ${monthSlots.size} slots for $yearMonth")

        // Print each slot's availability for debugging
        monthSlots.forEach { slot ->
            Log.d("BookingPage", "Slot: ${slot.date} - Slot ${slot.slotNumber} - Available: ${slot.isAvailable}")
        }

        // Update the current display list
        currentDisplayList.clear()
        currentDisplayList.addAll(monthSlots)

        // Force refresh the adapter
        slotAdapter.updateList(currentDisplayList)

        // Also notify the adapter directly
        slotAdapter.notifyDataSetChanged()

        // Force the RecyclerView to redraw
        slotsRecyclerView.adapter?.notifyDataSetChanged()

        Log.d("BookingPage", "Adapter updated with ${currentDisplayList.size} items")

        // Check if selected slot is still available
        selectedSlot?.let { selected ->
            val currentSlot = allSlotsMap[selected.slotId]
            if (currentSlot == null || !currentSlot.isAvailable) {
                Log.d("BookingPage", "Selected slot is no longer available!")
                selectedSlot = null
                slotAdapter.setSelectedSlot(null)
                Toast.makeText(this, "Your selected slot is no longer available", Toast.LENGTH_SHORT).show()
            }
        }

        // Extra: Force a small delay and refresh again (just to be sure)
        Handler(Looper.getMainLooper()).postDelayed({
            slotAdapter.notifyDataSetChanged()
            Log.d("BookingPage", "Second refresh triggered")
        }, 100)
    }

    private fun handleSlotSelection(selected: Slot) {
        if (!selected.isAvailable) {
            Toast.makeText(this, "This slot is already booked", Toast.LENGTH_SHORT).show()
            return
        }

        if (selected.slotNumber == 3) {
            val slotsOnSameDay = allSlotsMap.values.filter {
                it.date == selected.date && it.slotNumber in 1..2
            }
            if (slotsOnSameDay.any { !it.isAvailable }) {
                Toast.makeText(this, "Cannot book whole day - individual slots are already booked", Toast.LENGTH_SHORT).show()
                return
            }
            selectedSlot = selected
            Toast.makeText(this, "Selected: Whole Day (8AM - 7PM)", Toast.LENGTH_SHORT).show()
        } else {
            val wholeDaySlot = allSlotsMap.values.find {
                it.date == selected.date && it.slotNumber == 3
            }
            if (wholeDaySlot?.isAvailable == false) {
                Toast.makeText(this, "Cannot book - Whole day slot is already booked", Toast.LENGTH_SHORT).show()
                return
            }
            selectedSlot = selected
            Toast.makeText(this, "Selected: ${selected.startTime} - ${selected.endTime}", Toast.LENGTH_SHORT).show()
        }

        slotAdapter.setSelectedSlot(selectedSlot)
        updateDisplay() // Force refresh to show selection
    }

    private fun testUpdateSlot() {
        val yearMonth = if (currentMonthIndex == 0) "2026-04" else "2026-05"
        val availableSlots = allSlotsMap.values.filter {
            it.date.startsWith(yearMonth) && it.isAvailable
        }

        if (availableSlots.isNotEmpty()) {
            val slotToUpdate = availableSlots.first()
            Log.d("BookingPage", "Testing update for slot: ${slotToUpdate.slotId}")

            database.child("slots").child(slotToUpdate.slotId).child("isAvailable").setValue(false)
                .addOnSuccessListener {
                    Log.d("BookingPage", "✅ TEST: Successfully updated slot to unavailable!")
                    Toast.makeText(this, "Test update successful! The slot should now show as BOOKED", Toast.LENGTH_LONG).show()

                    // Force immediate refresh
                    Handler(Looper.getMainLooper()).postDelayed({
                        updateDisplay()
                    }, 500)
                }
                .addOnFailureListener { e ->
                    Log.e("BookingPage", "❌ TEST: Failed to update slot: ${e.message}")
                    Toast.makeText(this, "Test update failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "No available slots to test with. Create slots first!", Toast.LENGTH_LONG).show()
        }
    }

    private fun createAllSlotsManually() {
        Toast.makeText(this, "Creating slots...", Toast.LENGTH_SHORT).show()

        val slotsRef = database.child("slots")
        var createdCount = 0
        var totalSlots = 0

        for (date in weekendDates) {
            try {
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateObj = formatter.parse(date)
                val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(dateObj!!)

                createSlot(slotsRef, date, dayName, 1, "8:00 AM", "1:00 PM", false) { createdCount++ }
                createSlot(slotsRef, date, dayName, 2, "2:00 PM", "7:00 PM", false) { createdCount++ }
                createSlot(slotsRef, date, dayName, 3, "8:00 AM", "7:00 PM", true) { createdCount++ }
                totalSlots += 3
            } catch (e: Exception) {
                Log.e("BookingPage", "Error: ${e.message}")
            }
        }

        Toast.makeText(this, "Creating $totalSlots slots...", Toast.LENGTH_SHORT).show()
    }

    private fun createSlot(
        slotsRef: DatabaseReference,
        date: String,
        dayName: String,
        slotNumber: Int,
        startTime: String,
        endTime: String,
        isWholeDay: Boolean,
        onComplete: () -> Unit
    ) {
        val slotId = slotsRef.push().key ?: return

        val slot = Slot(
            slotId = slotId,
            hallName = hallName,
            date = date,
            dayName = dayName,
            slotNumber = slotNumber,
            startTime = startTime,
            endTime = endTime,
            isAvailable = true,
            isWholeDay = isWholeDay,
            createdBy = auth.currentUser?.uid ?: "",
            createdAt = System.currentTimeMillis()
        )

        slotsRef.child(slotId).setValue(slot)
            .addOnSuccessListener {
                onComplete()
                Log.d("BookingPage", "Created: $date - Slot $slotNumber")
            }
    }

    private fun proceedToAidSelection() {
        if (selectedSlot == null) {
            Toast.makeText(this, "Please select a slot", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, AidSelectionActivity::class.java)
        intent.putExtra("HALL_NAME", hallName)
        intent.putExtra("SLOT_ID", selectedSlot!!.slotId)
        intent.putExtra("SLOT_NUMBER", selectedSlot!!.slotNumber)
        intent.putExtra("DATE", selectedSlot!!.date)
        intent.putExtra("DAY_NAME", selectedSlot!!.dayName)
        intent.putExtra("START_TIME", selectedSlot!!.startTime)
        intent.putExtra("END_TIME", selectedSlot!!.endTime)
        intent.putExtra("IS_WHOLE_DAY", selectedSlot!!.isWholeDay)
        intent.putExtra("PRICE", if (selectedSlot!!.isWholeDay) "RM 500.00" else "RM 300.00")
        startActivity(intent)
    }
}