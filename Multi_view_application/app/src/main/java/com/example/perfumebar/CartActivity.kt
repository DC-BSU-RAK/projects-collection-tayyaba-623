package com.example.perfumebar

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CartActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var tvTotalPrice: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)

        container = findViewById(R.id.cartItemsContainer)
        val btnBack = findViewById<ImageView>(R.id.btn_back_cart)
        val btnCheckout = findViewById<Button>(R.id.btnCheckout)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)

        val mainLayout = findViewById<ViewGroup>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Use a custom font for a checkout button
        val risqueFont = ResourcesCompat.getFont(this, R.font.risque)
        btnCheckout.typeface = risqueFont

        // Footer section

        findViewById<LinearLayout>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<LinearLayout>(R.id.btn_cart).setOnClickListener {
            // Already in Cart, no action needed
        }

        // Functional Wishlist Icon
        findViewById<LinearLayout>(R.id.btn_wishlist).setOnClickListener {
            val intent = Intent(this, FavouriteActivity::class.java)
            startActivity(intent)
        }

        // Added listener for the Instructions/Guide activity
        findViewById<View>(R.id.btn_instructions).setOnClickListener {
            val intent = Intent(this, InstructionsActivity::class.java)
            startActivity(intent)
        }

        // --- END FOOTER NAVIGATION ---

        btnBack.setOnClickListener { finish() }

        btnCheckout.setOnClickListener {
            val sharedPref = getSharedPreferences("PerfumePrefs", Context.MODE_PRIVATE)
            val cartString = sharedPref.getString("cart_list_string", "") ?: ""

            if (cartString.isEmpty()) {
                Toast.makeText(this, "Bag is empty!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, CheckOutActivity::class.java)
                startActivity(intent)
            }
        }

        loadCartUI(shouldAnimate = true)
    }

    private fun loadCartUI(shouldAnimate: Boolean) {
        container.removeAllViews()
        val sharedPref = getSharedPreferences("PerfumePrefs", Context.MODE_PRIVATE)
        val cartString = sharedPref.getString("cart_list_string", "") ?: ""

        val rawItems = if (cartString.isEmpty()) emptyList() else cartString.split("###")
        val groupedItems = rawItems.groupingBy { it }.eachCount()

        var grandTotal = 0

        if (groupedItems.isEmpty()) {
            showEmptyMessage()
           // Display total price
            tvTotalPrice.text = "Total: 0 AED"
        } else {
            var index = 0
            groupedItems.forEach { (itemData, quantity) ->
                val parts = itemData.split("|")

                if (parts.size >= 2) {
                    val perfumeName = parts[0]
                    val imageResId = parts[1].toInt()
                    val price = if (parts.size == 3) parts[2].toInt() else 0
                    grandTotal += (price * quantity)

                    val card = createPerfumeCard(perfumeName, imageResId, quantity, price, itemData, shouldAnimate, index)
                    container.addView(card)
                    index++
                }
            }
            tvTotalPrice.text = "Total: $grandTotal AED"
        }
    }

   // Design background box for each perfume
    private fun createPerfumeCard(name: String, imgRes: Int, qty: Int, price: Int, rawData: String, animate: Boolean, index: Int): CardView {
        val risqueFont = ResourcesCompat.getFont(this, R.font.risque)

        val card = CardView(this).apply {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 50)
            layoutParams = params
            radius = 50f
            elevation = 10f
            setCardBackgroundColor("#4B4848".toColorInt())
            setContentPadding(40, 45, 40, 45)
            // Start animation
            if (animate) {
                alpha = 0f
                translationY = 100f
            }
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val img = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(200, 200)
            setImageResource(imgRes)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }

        val textContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setPadding(50, 0, 0, 0)
        }

        val txtName = TextView(this).apply {
            text = name
            setTextColor(Color.WHITE)
            textSize = 22f
            typeface = risqueFont
        }

      // Perfume quantity and total price
        val txtDetails = TextView(this).apply {
            text = "Qty: $qty | ${price * qty} AED"
            setTextColor(Color.parseColor("#CCCCCC"))
            textSize = 15f
            setPadding(0, 8, 0, 0)
            typeface = Typeface.DEFAULT
        }

        textContainer.addView(txtName)
        textContainer.addView(txtDetails)

        val deleteIcon = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(80, 80)
            setImageResource(android.R.drawable.ic_menu_delete)
            setColorFilter(Color.parseColor("#80FFFFFF"))
            setPadding(10, 10, 10, 10)
            setOnClickListener { removeItemCompletely(rawData) }
        }

        row.addView(img)
        row.addView(textContainer)
        row.addView(deleteIcon)
        card.addView(row)

        if (animate) {
            card.animate().alpha(1f).translationY(0f)
                .setDuration(600).setStartDelay(index * 100L)
                .setInterpolator(DecelerateInterpolator()).start()
        }
        return card
    }

// Delete item from cart when click on the delete icon
    private fun removeItemCompletely(itemData: String) {
        val sharedPref = getSharedPreferences("PerfumePrefs", Context.MODE_PRIVATE)
        val cartString = sharedPref.getString("cart_list_string", "") ?: ""
        val items = cartString.split("###").toMutableList()

        items.removeAll { it == itemData }

        val updatedString = items.joinToString("###")
        sharedPref.edit().putString("cart_list_string", updatedString).apply()

        loadCartUI(shouldAnimate = false)
        Toast.makeText(this, "Removed from bag", Toast.LENGTH_SHORT).show()
    }

   // Show empty message when cart is empty
    private fun showEmptyMessage() {
        val risqueFont = ResourcesCompat.getFont(this, R.font.risque)
        val emptyMsg = TextView(this).apply {
            text = "Your bag is currently empty."
            setTextColor(Color.WHITE)
            textSize = 20f
            gravity = Gravity.CENTER
            setPadding(0, 150, 0, 0)
            typeface = risqueFont
        }
        container.addView(emptyMsg)
    }
}