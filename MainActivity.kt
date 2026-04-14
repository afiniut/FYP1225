package com.example.hallbooking

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.hallbooking.adapters.ImagePagerAdapter
import com.example.hallbooking.databinding.ActivityMainBinding
import com.example.hallbooking.models.Hall
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var imageAdapter: ImagePagerAdapter

    // Sample hall data
    private val currentHall = Hall(
        id = "1",
        name = "Dewan Seri Utama",
        location = "Main Building, Level 2",
        capacity = 200,
        pricePerHour = 500.0,
        description = "A beautiful hall with air conditioning, sound system, and projector. Perfect for weddings, conferences, and events."
    )

    // Sample images (you can replace these with actual images)
    private val hallImages = listOf(
        R.drawable.hall1,
        R.drawable.hall2,
        R.drawable.hall3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Set up toolbar
        setupToolbar()

        // Display hall details
        displayHallDetails()

        // Set up image gallery with swipe
        setupImageGallery()

        // Choose Booking Slot button click
        // Choose Booking Slot button click
        binding.btnChooseSlot.setOnClickListener {
            val intent = Intent(this, BookingPageActivity::class.java)  // Changed to BookingPageActivity
            intent.putExtra("HALL_NAME", currentHall.name)
            startActivity(intent)
        }
    }

    private fun setupToolbar() {
        // Set the toolbar as action bar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Set up profile button click
        binding.ivProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun displayHallDetails() {
        binding.tvHallName.text = currentHall.name
        binding.tvLocation.text = "📍 ${currentHall.location}"
        binding.tvCapacity.text = "👥 Capacity: ${currentHall.capacity} people"
        binding.tvPrice.text = "💰 RM ${currentHall.pricePerHour}/hour"
        binding.tvDescription.text = currentHall.description
    }

    private fun setupImageGallery() {
        // Set up adapter for images
        imageAdapter = ImagePagerAdapter(hallImages)
        binding.viewPagerImages.adapter = imageAdapter

        // Connect TabLayout with ViewPager2 for page indicators
        TabLayoutMediator(binding.tabLayout, binding.viewPagerImages) { tab, position ->
            // No text needed, just using dots
        }.attach()
    }

    // Optional: Add menu if you want additional options
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // You can inflate a menu here if needed
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}