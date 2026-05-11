package com.example.perfumebar

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FavouriteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favourite)

        val mainView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        // Navigation section
        findViewById<LinearLayout>(R.id.btn_home).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.btn_cart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btn_wishlist).setOnClickListener {
        }

        updateCartBadge()
        loadWishlist()
    }

    private fun updateCartBadge() {
        val tvBadge = findViewById<TextView>(R.id.tvCartCountBadge)
        val sharedPref = getSharedPreferences("PerfumePrefs", Context.MODE_PRIVATE)
        val cartString = sharedPref.getString("cart_list_string", "") ?: ""

        if (cartString.isNotEmpty()) {
            val items = cartString.split("###").filter { it.isNotBlank() }
            val count = items.size

            if (count > 9) {
                tvBadge.text = "9+"
            } else {
                tvBadge.text = count.toString()
            }

            tvBadge.visibility = View.VISIBLE
        } else {
            tvBadge.visibility = View.GONE
        }
    }

    private fun loadWishlist() {
        val container = findViewById<LinearLayout>(R.id.wishlistContainer)
        val tvEmptyMessage = findViewById<TextView>(R.id.tvEmptyMessage)
        container.removeAllViews()

        val sharedPref = getSharedPreferences("PerfumePrefs", Context.MODE_PRIVATE)
        val favString = sharedPref.getString("wishlist_string", "") ?: ""

        if (favString.isNotEmpty()) {
// Hides empty message when item exist in the wishlist
            tvEmptyMessage.visibility = View.GONE
            val items = favString.split("###").filter { it.isNotBlank() }
            val risqueFont = ResourcesCompat.getFont(this, R.font.risque)

            items.forEachIndexed { index, item ->
             // Displays each wishlist item with their name, image and price
                val parts = item.split("|")
                if (parts.size == 3) {
                    val name = parts[0]
                    val imgRes = parts[1].toInt()
                    val price = parts[2]

                    val itemLayout = LinearLayout(this).apply {
                        orientation = LinearLayout.HORIZONTAL
                        setPadding(85, 10, 85, 10)
                        gravity = Gravity.CENTER_VERTICAL
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            450
                        )
                        params.setMargins(0, -60, 0, -60)
                        layoutParams = params
                        setBackgroundResource(R.drawable.favorite_bg)
                        alpha = 0f
                        translationY = 40f
                    }

                    val img = ImageView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(170, 170)
                        setImageResource(imgRes)
                        scaleType = ImageView.ScaleType.FIT_CENTER
                    }

                    val textContainer = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                        setPadding(50, 0, 0, 0)
                    }

                    val nameTv = TextView(this).apply {
                        text = name
                        setTextColor(Color.WHITE)
                        textSize = 21f
                        typeface = risqueFont
                    }

                    val priceTv = TextView(this).apply {
                        text = "$price AED"
                        setTextColor(Color.parseColor("#DDDDDD"))
                        textSize = 15f
                        setPadding(0, 5, 0, 0)
                    }

                  // Delete button to remove item from favorite list
                    val btnDelete = ImageView(this).apply {
                        layoutParams = LinearLayout.LayoutParams(70, 70)
                        setImageResource(android.R.drawable.ic_menu_delete)
                        setColorFilter(Color.WHITE)
                        setAlpha(0.8f)
                        setOnClickListener {
                            removeItem(item)
                            Toast.makeText(this@FavouriteActivity, "Removed from wishlist", Toast.LENGTH_SHORT).show()
                        }
                    }

                    textContainer.addView(nameTv)
                    textContainer.addView(priceTv)
                    itemLayout.addView(img)
                    itemLayout.addView(textContainer)
                    itemLayout.addView(btnDelete)
                    container.addView(itemLayout)

                    // Applies fade in and slide up animation
                    itemLayout.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(600)
                        .setStartDelay(index * 100L)
                        .setInterpolator(DecelerateInterpolator())
                        .start()
                }
            }
        } else {
            tvEmptyMessage.visibility = View.VISIBLE
        }
    }

    private fun removeItem(itemToRemove: String) {
        val sharedPref = getSharedPreferences("PerfumePrefs", Context.MODE_PRIVATE)
        val favString = sharedPref.getString("wishlist_string", "") ?: ""

        val updatedList = favString.split("###")
            .filter { it != itemToRemove && it.isNotBlank() }
            .joinToString("###")

        sharedPref.edit().putString("wishlist_string", updatedList).apply()
        loadWishlist()
    }
}