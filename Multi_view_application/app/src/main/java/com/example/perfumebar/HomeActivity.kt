package com.example.perfumebar

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import de.hdodenhof.circleimageview.CircleImageView

class HomeActivity : AppCompatActivity() {

    private lateinit var cartBadge: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        val mainLayout = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPref = getSharedPreferences("PerfumePrefs", Context.MODE_PRIVATE)
        cartBadge = findViewById(R.id.cart_badge)


        val profileImageHeader = findViewById<CircleImageView>(R.id.profile_image_header)
        //   Display saved profile image
        val imageUriString = sharedPref.getString("user_image_uri", null)

        // Check if profile image exists
        if (!imageUriString.isNullOrEmpty()) {
            try {
                val imageUri = Uri.parse(imageUriString)
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(imageUri, takeFlags)
                profileImageHeader.setImageURI(imageUri)
            } catch (e: Exception) {
                Log.e("HomeActivity", "Permission failed: ${e.message}")
                try {
                    profileImageHeader.setImageURI(Uri.parse(imageUriString))
                } catch (innerException: Exception) {
                    profileImageHeader.setImageResource(R.drawable.profile_image)
                }
            }
        }

      // Navigate to profile screen when the profile icon is clicked
        profileImageHeader.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        val welcomeText = findViewById<TextView>(R.id.textView4)
        val rawName = intent.getStringExtra("EXTRA_NAME") ?: sharedPref.getString("user_name", null)

        if (!rawName.isNullOrEmpty()) {
            val displayName = if (rawName.contains("@")) rawName.substringBefore("@") else rawName
            val formattedName = displayName.replaceFirstChar { it.uppercase() }
            welcomeText.text = "Welcome $formattedName!"
        } else {
            welcomeText.text = "Welcome Guest"
        }

        // Perfume Mood clickable boxes
        findViewById<ImageView>(R.id.imageView5).setOnClickListener { startResultActivity("Energetic") }
        findViewById<ImageView>(R.id.imageView6).setOnClickListener { startResultActivity("Calm") }
        findViewById<ImageView>(R.id.imageView7).setOnClickListener { startResultActivity("Happy") }
        findViewById<ImageView>(R.id.imageView8).setOnClickListener { startResultActivity("Confidence") }

        // Footer section
        // Opens home screen
        findViewById<View>(R.id.btn_home).setOnClickListener {
            findViewById<ScrollView>(R.id.homeScrollView).smoothScrollTo(0, 0)
        }

        // Opens cart screen
        findViewById<View>(R.id.btn_cart).setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        // Opens wishlist screen
        findViewById<View>(R.id.btn_wishlist).setOnClickListener {
            val intent = Intent(this, FavouriteActivity::class.java)
            startActivity(intent)
        }

        // Opens instructions screen
        findViewById<View>(R.id.btn_instructions).setOnClickListener {
            val intent = Intent(this, InstructionsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshCartBadge()
    }

   // Update cart badge to show selected items
    private fun refreshCartBadge() {
        val sharedPref = getSharedPreferences("PerfumePrefs", Context.MODE_PRIVATE)
        val cartString = sharedPref.getString("cart_list_string", "") ?: ""
        val items = if (cartString.isEmpty()) emptyList<String>() else cartString.split("###")
       //  Count selected items
       val count = items.size

     //  Show number if items exist
        if (count > 0) {
            cartBadge.visibility = View.VISIBLE
      //  Displays 9+ if selected items are more than 9
            cartBadge.text = if (count > 9) "9+" else count.toString()
        } else {
            cartBadge.visibility = View.GONE
        }
    }

    private fun startResultActivity(mood: String) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("MOOD", mood)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
    }
}