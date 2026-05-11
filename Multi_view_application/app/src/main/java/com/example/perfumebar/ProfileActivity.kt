package com.example.perfumebar

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // --- 1. INITIALIZE VIEWS ---
        val profileImage = findViewById<CircleImageView>(R.id.imageView13)
        val tvUsernameLabel = findViewById<TextView>(R.id.textView5)
        val tvPasswordValue = findViewById<TextView>(R.id.textView8)
        val logoutButton = findViewById<ImageView>(R.id.imageView14)
        val editIcon = findViewById<ImageView>(R.id.imageView15)
        val passwordToggle = findViewById<ImageView>(R.id.ivPasswordToggle)
        val backButton = findViewById<ImageView>(R.id.imageView16)

        // --- 2. FETCH SHARED DATA ---
        val sharedPref = getSharedPreferences("PerfumePrefs", Context.MODE_PRIVATE)
        val savedName = sharedPref.getString("user_name", "Guest")
        val savedPass = sharedPref.getString("user_password", "********")
        val savedImgUriString = sharedPref.getString("user_image_uri", null)

        // --- 3. APPLY DATA ---
        tvUsernameLabel?.text = "Username: $savedName"

        val passwordLength = savedPass?.length ?: 8
        tvPasswordValue?.text = "•".repeat(passwordLength)

        savedImgUriString?.let { uriString ->
            profileImage?.let { imageView ->
                try {
                    val userPhotoUri = Uri.parse(uriString)
                    imageView.setImageURI(userPhotoUri)
                } catch (e: Exception) {
                    Log.e("ProfileActivity", "Error setting image: ${e.message}")
                }
            }
        }

        // --- 4. BACK NAVIGATION ---
        backButton?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // --- 5. PASSWORD TOGGLE ---
        passwordToggle?.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                tvPasswordValue?.text = savedPass
                passwordToggle.setImageResource(R.drawable.ic_eye_on)
            } else {
                val maskLength = savedPass?.length ?: 8
                tvPasswordValue?.text = "•".repeat(maskLength)
                passwordToggle.setImageResource(R.drawable.ic_eye_off)
            }
        }

        // --- 6. LOGOUT NAVIGATION ---
        logoutButton?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // --- 7. EDIT ICON ---
        editIcon?.setOnClickListener {
            Log.d("ProfileActivity", "Edit clicked")
        }

        // --- 8. SYSTEM UI ADJUSTMENTS ---
        val mainView = findViewById<android.view.View>(R.id.main)
        mainView?.let { view ->
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}