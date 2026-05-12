package com.example.perfumebar

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import de.hdodenhof.circleimageview.CircleImageView

class LoginActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    // Launcher to pick an image 
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                // 1. Grant "Persistable" permission so other activities can see this image
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                contentResolver.takePersistableUriPermission(it, takeFlags)

                // Save the URI and update the UI
                selectedImageUri = it
                val profileImage = findViewById<CircleImageView>(R.id.imageView2)
                profileImage.setImageURI(it)

            } catch (e: Exception) {
                selectedImageUri = it
                findViewById<CircleImageView>(R.id.imageView2).setImageURI(it)
                Toast.makeText(this, "Limited image access granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnSignIn = findViewById<Button>(R.id.buttonSignin)
        val addIcon = findViewById<ImageView>(R.id.imageView3)

        addIcon.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnSignIn.setOnClickListener {
            val userEmail = etEmail.text.toString().trim()
            val userPassword = etPassword.text.toString().trim()

            if (userEmail.isNotEmpty() && userPassword.isNotEmpty()) {
                val sharedPref = getSharedPreferences("PerfumePrefs", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()

                editor.putString("user_name", userEmail)
                editor.putString("user_password", userPassword)

                // Save URI string only if an image was actually picked
                selectedImageUri?.let {
                    editor.putString("user_image_uri", it.toString())
                }

                editor.apply()

                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("EXTRA_NAME", userEmail)
                startActivity(intent)
                finish()
            } else {
                if (userEmail.isEmpty()) etEmail.error = "Enter username"
                if (userPassword.isEmpty()) etPassword.error = "Enter password"
            }
        }
    }
}
