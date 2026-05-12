package com.example.perfumebar

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CheckOutActivity : AppCompatActivity() {

    private var selectedPaymentMethod: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_check_out)

        // Adjust UI for Status Bar
        val mainView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        val risqueFont = ResourcesCompat.getFont(this, R.font.risque)

        // UI Elements
        val btnDone       = findViewById<Button>(R.id.btnDone)
        val cardCash      = findViewById<MaterialCardView>(R.id.cardCash)
        val cardCard      = findViewById<MaterialCardView>(R.id.cardCard)
        val iconCashCheck = findViewById<ImageView>(R.id.iconCashCheck)
        val iconCardCheck = findViewById<ImageView>(R.id.iconCardCheck)
        val btnBack       = findViewById<ImageView>(R.id.btnBack)

        btnDone.typeface = risqueFont

        // FOOTER NAVIGATION 
        findViewById<LinearLayout>(R.id.btn_home).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.btn_cart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.btn_wishlist).setOnClickListener {
            startActivity(Intent(this, FavouriteActivity::class.java))
            finish()
        }

        // Added listener for the Instructions/Guide activity
        findViewById<View>(R.id.btn_instructions).setOnClickListener {
            val intent = Intent(this, InstructionsActivity::class.java)
            startActivity(intent)
        }

        btnBack.setOnClickListener { finish() }

        // PAYMENT LOGIC 
        cardCash.setOnClickListener {
            selectedPaymentMethod = "Cash"
            highlightSelection(cardCash, cardCard, iconCashCheck, iconCardCheck)
        }

        cardCard.setOnClickListener {
            selectedPaymentMethod = "Card"
            highlightSelection(cardCard, cardCash, iconCardCheck, iconCashCheck)
        }

        btnDone.setOnClickListener {
            if (selectedPaymentMethod.isEmpty()) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
            } else {
                showSuccessDialog()
            }
        }
    }

    private fun showSuccessDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.order_screen)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnBackHome    = dialog.findViewById<Button>(R.id.btnBackHome)
        val tvSuccessTitle = dialog.findViewById<TextView>(R.id.tvSuccessTitle)
        val ivSuccess      = dialog.findViewById<ImageView>(R.id.ivSuccess)

        val risqueFont = ResourcesCompat.getFont(this, R.font.risque)
        tvSuccessTitle.typeface = risqueFont
        btnBackHome.typeface    = risqueFont

        startBagAnimation(ivSuccess)

        btnBackHome.setOnClickListener {
            val sharedPref = getSharedPreferences("PerfumePrefs", Context.MODE_PRIVATE)
            sharedPref.edit().putString("cart_list_string", "").apply()
            dialog.dismiss()

            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        dialog.setCancelable(false)
        dialog.show()
    }

    private fun startBagAnimation(imageView: ImageView) {
        val floatAnim = TranslateAnimation(0f, 0f, 0f, -40f).apply {
            duration = 1800
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        imageView.startAnimation(floatAnim)
    }

    private fun highlightSelection(
        selected: MaterialCardView,
        unselected: MaterialCardView,
        selectedIcon: ImageView,
        unselectedIcon: ImageView
    ) {
        selected.setCardBackgroundColor(Color.parseColor("#FFF7F8"))
        selected.strokeWidth = 4
        selected.strokeColor = Color.parseColor("#65000B")
        selected.cardElevation = 10f
        selectedIcon.visibility = View.VISIBLE

        unselected.setCardBackgroundColor(Color.WHITE)
        unselected.strokeWidth = 0
        unselected.cardElevation = 4f
        unselectedIcon.visibility = View.INVISIBLE
    }
}
