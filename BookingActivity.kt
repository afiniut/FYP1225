package com.example.hallbooking

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class BookingActivity : AppCompatActivity() {

    private var selectedSlot: String? = null

    // Declare views
    private lateinit var tvHallName: TextView
    private lateinit var cardSlot1: CardView
    private lateinit var cardSlot2: CardView
    private lateinit var cardSlot3: CardView
    private lateinit var btnProceed: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        // Initialize views using findViewById
        tvHallName = findViewById(R.id.tvBookingHallName)
        cardSlot1 = findViewById(R.id.cardSlot1)
        cardSlot2 = findViewById(R.id.cardSlot2)
        cardSlot3 = findViewById(R.id.cardSlot3)
        btnProceed = findViewById(R.id.btnProceed)

        // Get hall name from intent
        val hallName = intent.getStringExtra("HALL_NAME") ?: "Hall"
        tvHallName.text = hallName

        // Set up click listeners
        setupSlotClickListeners()

        // Proceed button click - UPDATED to go to AidSelectionActivity
        btnProceed.setOnClickListener {
            if (selectedSlot != null) {
                // Navigate to Aid Selection page
                val intent = Intent(this, AidSelectionActivity::class.java)
                intent.putExtra("SELECTED_SLOT", selectedSlot)
                intent.putExtra("HALL_NAME", tvHallName.text.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select a slot first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSlotClickListeners() {
        cardSlot1.setOnClickListener {
            selectSlot("SLOT 1 (Sat 10AM-1PM)", cardSlot1)
        }

        cardSlot2.setOnClickListener {
            selectSlot("SLOT 2 (Sat 2PM-5PM)", cardSlot2)
        }

        cardSlot3.setOnClickListener {
            selectSlot("SLOT 3 (Sun 10AM-1PM)", cardSlot3)
        }
    }

    private fun selectSlot(slotName: String, selectedCard: CardView) {
        selectedSlot = slotName
        btnProceed.isEnabled = true

        // Debug prints
        println("DEBUG: Slot selected: $slotName")
        println("DEBUG: Button enabled: ${btnProceed.isEnabled}")

        resetAllCards()
        selectedCard.setCardBackgroundColor(Color.parseColor("#E3F2FD"))
        Toast.makeText(this, "$slotName selected", Toast.LENGTH_SHORT).show()
    }

    private fun resetAllCards() {
        cardSlot1.setCardBackgroundColor(Color.WHITE)
        cardSlot2.setCardBackgroundColor(Color.WHITE)
        cardSlot3.setCardBackgroundColor(Color.WHITE)
    }
}