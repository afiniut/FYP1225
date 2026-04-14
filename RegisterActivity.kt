package com.example.hallbooking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hallbooking.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set click listener for register button
        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        // Set click listener for login text (to go back to login screen)
        binding.tvLogin.setOnClickListener {
            // Go back to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        // Get values from input fields
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // Validate inputs
        if (!validateInputs(name, email, phone, password, confirmPassword)) {
            return
        }

        // Disable button to prevent double submission
        binding.btnRegister.isEnabled = false

        // Create user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Re-enable button
                binding.btnRegister.isEnabled = true

                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Registration successful! Please login.",
                        Toast.LENGTH_LONG
                    ).show()

                    // Go back to Login screen
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun validateInputs(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        return when {
            name.isEmpty() -> {
                binding.etName.error = "Name is required"
                false
            }
            email.isEmpty() -> {
                binding.etEmail.error = "Email is required"
                false
            }
            phone.isEmpty() -> {
                binding.etPhone.error = "Phone number is required"
                false
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Password is required"
                false
            }
            password.length < 6 -> {
                binding.etPassword.error = "Password must be at least 6 characters"
                false
            }
            confirmPassword.isEmpty() -> {
                binding.etConfirmPassword.error = "Please confirm your password"
                false
            }
            password != confirmPassword -> {
                binding.etConfirmPassword.error = "Passwords do not match"
                false
            }
            else -> true
        }
    }
}