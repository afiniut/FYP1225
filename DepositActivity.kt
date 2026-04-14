package com.example.hallbooking

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class DepositActivity : AppCompatActivity() {

    private lateinit var tvHallName: TextView
    private lateinit var tvSelectedSlot: TextView
    private lateinit var tvDepositAmount: TextView
    private lateinit var tvAidsSummary: TextView
    private lateinit var btnConfirmDeposit: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var btnBackToAidPage: MaterialButton  // NEW

    // Deposit amount (you can change this or get from hall data)
    private val depositAmount = 100.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deposit)

        // DEBUG: Check what data we received
        val receivedHallName = intent.getStringExtra("HALL_NAME")
        val receivedSlot = intent.getStringExtra("SELECTED_SLOT")
        val receivedAids = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("SELECTED_AIDS", HashMap::class.java) as? HashMap<String, Int>
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("SELECTED_AIDS") as? HashMap<String, Int>
        }

        println("🔴 DepositActivity - Received in onCreate:")
        println("   Hall: '$receivedHallName'")
        println("   Slot: '$receivedSlot'")
        println("   Aids: $receivedAids")

        // Initialize views
        initializeViews()

        // Get data from intent (with null safety)
        val hallName = receivedHallName ?: "Hall"
        val selectedSlot = receivedSlot ?: "Slot not selected"

        // Display information
        displayBookingInfo(hallName, selectedSlot, receivedAids)

        // Set up button listeners
        setupClickListeners(hallName, selectedSlot, receivedAids)
    }

    private fun initializeViews() {
        tvHallName = findViewById(R.id.tvDepositHallName)
        tvSelectedSlot = findViewById(R.id.tvDepositSlot)
        tvDepositAmount = findViewById(R.id.tvDepositAmount)
        tvAidsSummary = findViewById(R.id.tvAidsSummary)
        btnConfirmDeposit = findViewById(R.id.btnConfirmDeposit)
        btnCancel = findViewById(R.id.btnCancel)
        btnBackToAidPage = findViewById(R.id.btnBackToAidPage)  // NEW
    }

    private fun displayBookingInfo(hallName: String, selectedSlot: String, selectedAids: HashMap<String, Int>?) {
        tvHallName.text = hallName
        tvSelectedSlot.text = selectedSlot
        tvDepositAmount.text = "RM $depositAmount"

        // Show summary of selected aids
        if (selectedAids != null && selectedAids.isNotEmpty()) {
            val aidsList = selectedAids.map { "${it.key} x${it.value}" }.joinToString(", ")
            tvAidsSummary.text = "Aids: $aidsList"
            tvAidsSummary.visibility = TextView.VISIBLE
        } else {
            tvAidsSummary.text = "No aids selected"
            tvAidsSummary.visibility = TextView.VISIBLE
        }
    }

    private fun setupClickListeners(hallName: String, selectedSlot: String, selectedAids: HashMap<String, Int>?) {
        // NEW: Back to Aid Page button
        btnBackToAidPage.setOnClickListener {
            Toast.makeText(this, "Going back to aid selection", Toast.LENGTH_SHORT).show()

            // Navigate back to AidSelectionActivity with all data
            val intent = Intent(this, AidSelectionActivity::class.java)
            intent.putExtra("HALL_NAME", hallName)
            intent.putExtra("SELECTED_SLOT", selectedSlot)
            if (selectedAids != null && selectedAids.isNotEmpty()) {
                intent.putExtra("SELECTED_AIDS", selectedAids)
            }

            startActivity(intent)
            finish() // Close DepositActivity so user can't come back
        }

        // CONFIRM DEPOSIT button - goes to ReceiptActivity
        btnConfirmDeposit.setOnClickListener {
            Toast.makeText(this, "Processing deposit...", Toast.LENGTH_SHORT).show()

            // Go to Receipt Page
            val intent = Intent(this, ReceiptActivity::class.java)
            intent.putExtra("HALL_NAME", hallName)
            intent.putExtra("SELECTED_SLOT", selectedSlot)
            if (selectedAids != null && selectedAids.isNotEmpty()) {
                intent.putExtra("SELECTED_AIDS", selectedAids)
            }
            intent.putExtra("DEPOSIT_AMOUNT", depositAmount)
            intent.putExtra("USER_NAME", "Ahmad Bin Abdullah") // You can get this from your login system

            startActivity(intent)
            finish() // Close DepositActivity
        }

        // CANCEL button
        btnCancel.setOnClickListener {
            showCancelConfirmationDialog(hallName, selectedSlot, selectedAids)
        }
    }

    private fun showCancelConfirmationDialog(hallName: String, selectedSlot: String, selectedAids: HashMap<String, Int>?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_cancel_confirmation, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // YES button
        dialogView.findViewById<MaterialButton>(R.id.btnDialogYes).setOnClickListener {
            Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show()
            // Go back to MainActivity (home)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        // NO button
        dialogView.findViewById<MaterialButton>(R.id.btnDialogNo).setOnClickListener {
            dialog.dismiss() // Just close dialog, stay on deposit page
        }

        dialog.show()
    }
}