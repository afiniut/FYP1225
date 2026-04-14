package com.example.hallbooking

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class FirebaseTestActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var btnTest: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_test)

        tvStatus = findViewById(R.id.tvStatus)
        btnTest = findViewById(R.id.btnTest)

        btnTest.setOnClickListener {
            testFirebase()
        }
    }

    private fun testFirebase() {
        tvStatus.text = "Testing Firebase..."
        Log.d("FirebaseTest", "Starting test...")

        try {
            val database = FirebaseDatabase.getInstance()
            val testRef = database.reference.child("test_from_app")

            testRef.setValue(mapOf(
                "message" to "Hello from app!",
                "timestamp" to System.currentTimeMillis()
            )).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseTest", "✅ SUCCESS! Data written to Firebase!")
                    tvStatus.text = "✅ SUCCESS! Check Firebase Console!"
                    Toast.makeText(this, "Firebase works! ✅", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("FirebaseTest", "❌ FAILED: ${task.exception?.message}")
                    tvStatus.text = "❌ FAILED: ${task.exception?.message}"
                    Toast.makeText(this, "Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseTest", "Exception: ${e.message}")
            tvStatus.text = "Exception: ${e.message}"
            Toast.makeText(this, "Exception: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}