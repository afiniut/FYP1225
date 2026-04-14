package com.example.hallbooking

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton

class AidSelectionActivity : AppCompatActivity() {

    // Declare views
    private lateinit var cardTables: CardView
    private lateinit var cardChairs: CardView
    private lateinit var cardProjector: CardView
    private lateinit var cardSoundSystem: CardView

    private lateinit var checkBoxTables: CheckBox
    private lateinit var checkBoxChairs: CheckBox
    private lateinit var checkBoxProjector: CheckBox
    private lateinit var checkBoxSoundSystem: CheckBox

    private lateinit var btnProceed: MaterialButton
    private lateinit var btnNoThanks: MaterialButton

    // Store data from BookingPageActivity
    private var hallName: String = ""
    private var slotId: String = ""
    private var slotNumber: Int = 0
    private var date: String = ""
    private var dayName: String = ""
    private var startTime: String = ""
    private var endTime: String = ""
    private var isWholeDay: Boolean = false
    private var price: String = ""
    private var slotDisplay: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aid_selection)

        // Get data from intent (from BookingPageActivity)
        getIntentData()

        // Initialize views
        initializeViews()

        // Set up click listeners
        setupClickListeners()
    }

    private fun getIntentData() {
        hallName = intent.getStringExtra("HALL_NAME") ?: "Dewan Seri Utama"
        slotId = intent.getStringExtra("SLOT_ID") ?: ""
        slotNumber = intent.getIntExtra("SLOT_NUMBER", 0)
        date = intent.getStringExtra("DATE") ?: ""
        dayName = intent.getStringExtra("DAY_NAME") ?: ""
        startTime = intent.getStringExtra("START_TIME") ?: ""
        endTime = intent.getStringExtra("END_TIME") ?: ""
        isWholeDay = intent.getBooleanExtra("IS_WHOLE_DAY", false)
        price = intent.getStringExtra("PRICE") ?: "RM 300.00"

        // Format slot display string
        slotDisplay = if (isWholeDay) {
            "Whole Day (8:00 AM - 7:00 PM) - $price"
        } else {
            "$startTime - $endTime - $price"
        }
    }

    private fun initializeViews() {
        // Cards
        cardTables = findViewById(R.id.cardTables)
        cardChairs = findViewById(R.id.cardChairs)
        cardProjector = findViewById(R.id.cardProjector)
        cardSoundSystem = findViewById(R.id.cardSoundSystem)

        // Checkboxes
        checkBoxTables = findViewById(R.id.checkBoxTables)
        checkBoxChairs = findViewById(R.id.checkBoxChairs)
        checkBoxProjector = findViewById(R.id.checkBoxProjector)
        checkBoxSoundSystem = findViewById(R.id.checkBoxSoundSystem)

        // Buttons
        btnProceed = findViewById(R.id.btnProceed)
        btnNoThanks = findViewById(R.id.btnNoThanks)
    }

    private fun setupClickListeners() {
        // Card click listeners to toggle checkboxes
        cardTables.setOnClickListener {
            checkBoxTables.isChecked = !checkBoxTables.isChecked
        }

        cardChairs.setOnClickListener {
            checkBoxChairs.isChecked = !checkBoxChairs.isChecked
        }

        cardProjector.setOnClickListener {
            checkBoxProjector.isChecked = !checkBoxProjector.isChecked
        }

        cardSoundSystem.setOnClickListener {
            checkBoxSoundSystem.isChecked = !checkBoxSoundSystem.isChecked
        }

        // PROCEED button - goes to ConfirmationActivity with selected aids
        btnProceed.setOnClickListener {
            val selectedAids = getSelectedItems()
            val totalDeposit = calculateTotalDeposit(selectedAids)

            val intent = Intent(this, ConfirmationActivity::class.java)

            // Pass hall and slot details
            intent.putExtra("HALL_NAME", hallName)
            intent.putExtra("SLOT_ID", slotId)
            intent.putExtra("SLOT_NUMBER", slotNumber)
            intent.putExtra("DATE", date)
            intent.putExtra("DAY_NAME", dayName)
            intent.putExtra("START_TIME", startTime)
            intent.putExtra("END_TIME", endTime)
            intent.putExtra("IS_WHOLE_DAY", isWholeDay)
            intent.putExtra("PRICE", price)

            // Pass aids and deposit
            if (selectedAids.isNotEmpty()) {
                intent.putExtra("SELECTED_AIDS", selectedAids.toTypedArray())
            }
            intent.putExtra("TOTAL_DEPOSIT", totalDeposit)

            startActivity(intent)
        }

        // NO, THANK YOU button - proceed without aids
        btnNoThanks.setOnClickListener {
            showSkipAidsConfirmationDialog()
        }
    }

    private fun calculateTotalDeposit(selectedAids: List<String>): Double {
        // Base deposit from slot (50% of slot price)
        val slotPrice = if (isWholeDay) 500.00 else 300.00
        val baseDeposit = slotPrice * 0.5

        // Add deposit for aids (50% of aid price)
        var aidsDeposit = 0.0
        val aidPrices = mapOf(
            "Tables" to 50.00,
            "Chairs" to 30.00,
            "Projector" to 100.00,
            "Sound System" to 80.00
        )

        selectedAids.forEach { aidName ->
            val price = aidPrices[aidName] ?: 0.0
            aidsDeposit += price * 0.5
        }

        return baseDeposit + aidsDeposit
    }

    private fun getSelectedItems(): List<String> {
        val selectedItems = mutableListOf<String>()

        if (checkBoxTables.isChecked) selectedItems.add("Tables")
        if (checkBoxChairs.isChecked) selectedItems.add("Chairs")
        if (checkBoxProjector.isChecked) selectedItems.add("Projector")
        if (checkBoxSoundSystem.isChecked) selectedItems.add("Sound System")

        return selectedItems
    }

    private fun showSkipAidsConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_no_aids_confirmation, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val tvMessage = dialogView.findViewById<TextView>(R.id.tvDialogMessage)
        tvMessage.text = "Are you sure you want to skip aid selection? You can still proceed without any aids."

        dialogView.findViewById<MaterialButton>(R.id.btnDialogYes).setOnClickListener {
            // Proceed without aids
            val intent = Intent(this, ConfirmationActivity::class.java)

            // Pass hall and slot details
            intent.putExtra("HALL_NAME", hallName)
            intent.putExtra("SLOT_ID", slotId)
            intent.putExtra("SLOT_NUMBER", slotNumber)
            intent.putExtra("DATE", date)
            intent.putExtra("DAY_NAME", dayName)
            intent.putExtra("START_TIME", startTime)
            intent.putExtra("END_TIME", endTime)
            intent.putExtra("IS_WHOLE_DAY", isWholeDay)
            intent.putExtra("PRICE", price)

            // No aids
            intent.putExtra("TOTAL_DEPOSIT", if (isWholeDay) 250.00 else 150.00)

            startActivity(intent)
            Toast.makeText(this, "Proceeding without aids", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialogView.findViewById<MaterialButton>(R.id.btnDialogNo).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}