package com.example.hallbooking

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class ReceiptActivity : AppCompatActivity() {

    private lateinit var tvReceiptName: TextView
    private lateinit var tvReceiptAmount: TextView
    private lateinit var tvReceiptSlot: TextView
    private lateinit var tvReceiptHall: TextView
    private lateinit var tvReceiptRef: TextView
    private lateinit var btnDownload: MaterialButton
    private lateinit var btnShare: MaterialButton
    private lateinit var btnBackToHome: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)

        initializeViews()

        // Get data from intent (from ConfirmationActivity)
        val hallName = intent.getStringExtra("HALL_NAME") ?: "Dewan Seri Utama"
        val slotNumber = intent.getIntExtra("SLOT_NUMBER", 0)
        val date = intent.getStringExtra("DATE") ?: ""
        val dayName = intent.getStringExtra("DAY_NAME") ?: ""
        val startTime = intent.getStringExtra("START_TIME") ?: ""
        val endTime = intent.getStringExtra("END_TIME") ?: ""
        val isWholeDay = intent.getBooleanExtra("IS_WHOLE_DAY", false)
        val price = intent.getStringExtra("PRICE") ?: "RM 300.00"
        val totalDeposit = intent.getDoubleExtra("TOTAL_DEPOSIT", 0.0)

        // Get user name from Firebase Auth
        val userName = FirebaseAuth.getInstance().currentUser?.displayName ?: "Customer"

        // Format slot display
        val slotDisplay = if (isWholeDay) {
            "Slot 3: Whole Day (8:00 AM - 7:00 PM)"
        } else {
            "Slot $slotNumber: $startTime - $endTime"
        }
        val fullSlotText = "$dayName, $date\n$slotDisplay"

        // Generate a receipt/booking reference
        val bookingRef = generateBookingReference()

        // Display data
        tvReceiptName.text = userName
        tvReceiptAmount.text = "RM ${String.format("%.2f", totalDeposit)}"
        tvReceiptSlot.text = fullSlotText
        tvReceiptHall.text = hallName
        tvReceiptRef.text = bookingRef

        // Set up button listeners
        setupClickListeners()
    }

    private fun initializeViews() {
        tvReceiptName = findViewById(R.id.tvReceiptName)
        tvReceiptAmount = findViewById(R.id.tvReceiptAmount)
        tvReceiptSlot = findViewById(R.id.tvReceiptSlot)
        tvReceiptHall = findViewById(R.id.tvReceiptHall)
        tvReceiptRef = findViewById(R.id.tvReceiptRef)
        btnDownload = findViewById(R.id.btnDownloadReceipt)
        btnShare = findViewById(R.id.btnShareReceipt)
        btnBackToHome = findViewById(R.id.btnBackToHome)
    }

    private fun generateBookingReference(): String {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        return "SRA${timestamp.takeLast(8)}"
    }

    private fun setupClickListeners() {
        btnDownload.setOnClickListener {
            Toast.makeText(this, "Downloading receipt...", Toast.LENGTH_SHORT).show()
        }

        btnShare.setOnClickListener {
            Toast.makeText(this, "Sharing receipt...", Toast.LENGTH_SHORT).show()
        }

        btnBackToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}