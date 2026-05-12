package com.example.perfumebar

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ResultActivity : AppCompatActivity() {

    data class PerfumeData(
        val name: String,
        val imageRes: Int,
        val price: Int
    )

    private lateinit var tvCartBadge: TextView
    private val currentMoodPerfumes = mutableListOf<PerfumeData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)

        // SYSTEM UI ADJUSTMENTS
        // This finds the root layout automatically so you don't need to add an ID in XML
        val rootLayout = findViewById<View>(android.R.id.content).getRootView()

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        tvCartBadge = findViewById(R.id.tvCartBadge)

        val risqueFont = ResourcesCompat.getFont(this, R.font.risque)
        val tvMoodTitle = findViewById<TextView>(R.id.tvMoodTitle)
        val tvSubtitle = findViewById<TextView>(R.id.tvSubtitle)

        tvMoodTitle.typeface = risqueFont
        tvSubtitle.typeface = risqueFont

        val mood = intent.getStringExtra("MOOD") ?: "Calm"
        tvMoodTitle.text = "$mood Mode"
        tvSubtitle.text = "Perfumes picked for your mood"

        updatePerfumeContent(mood)

        findViewById<ImageView>(R.id.icon_back).setOnClickListener { finish() }

        // FOOTER NAVIGATION 
        findViewById<View>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<View>(R.id.btn_cart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        findViewById<View>(R.id.btn_wishlist).setOnClickListener {
            startActivity(Intent(this, FavouriteActivity::class.java))
        }

        // Added listener for the Instructions/Guide activity
        findViewById<View>(R.id.btn_instructions).setOnClickListener {
            val intent = Intent(this, InstructionsActivity::class.java)
            startActivity(intent)
        }

        setupFavoriteButtons()
        setupCartLogic()
        setupAnimations()
    }

    override fun onResume() {
        super.onResume()
        refreshBadge()
        syncFavoriteIcons()
    }

    private fun setupFavoriteButtons() {
        val favButtons = listOf(
            R.id.fav1, R.id.fav2, R.id.fav3,
            R.id.fav4, R.id.fav5, R.id.fav6
        )
        val sharedPref = getSharedPreferences("PerfumePrefs", Context.MODE_PRIVATE)

        favButtons.forEachIndexed { index, id ->
            val btn = findViewById<ImageButton>(id)

            btn?.setOnClickListener {
                if (index >= currentMoodPerfumes.size) return@setOnClickListener

                val selected = currentMoodPerfumes[index]
                var favString = sharedPref.getString("wishlist_string", "") ?: ""

                val isCurrentlyLiked = favString.contains(selected.name)

                if (!isCurrentlyLiked) {
                    val entry = "${selected.name}|${selected.imageRes}|${selected.price}"
                    favString = if (favString.isEmpty()) entry else "$favString###$entry"
                    btn.setColorFilter(Color.parseColor("#65000B"))
                    Toast.makeText(this, "${selected.name} added to Wishlist", Toast.LENGTH_SHORT).show()
                } else {
                    val updatedList = favString.split("###")
                        .filter { it.split("|")[0] != selected.name }
                        .joinToString("###")
                    favString = updatedList
                    btn.setColorFilter(Color.WHITE)
                    Toast.makeText(this, "${selected.name} removed from Wishlist", Toast.LENGTH_SHORT).show()
                }
                sharedPref.edit().putString("wishlist_string", favString).apply()
            }
        }
    }

    private fun syncFavoriteIcons() {
        val favButtons = listOf(
            R.id.fav1, R.id.fav2, R.id.fav3,
            R.id.fav4, R.id.fav5, R.id.fav6
        )
        val sharedPref = getSharedPreferences("PerfumePrefs", Context.MODE_PRIVATE)
        val favString = sharedPref.getString("wishlist_string", "") ?: ""

        favButtons.forEachIndexed { index, id ->
            val btn = findViewById<ImageButton>(id)
            if (index < currentMoodPerfumes.size) {
                val perfumeName = currentMoodPerfumes[index].name
                if (favString.contains(perfumeName)) {
                    btn?.setColorFilter(Color.parseColor("#65000B"))
                } else {
                    btn?.setColorFilter(Color.WHITE)
                }
            }
        }
    }

    private fun refreshBadge() {
        val sharedPref = getSharedPreferences("PerfumePrefs", Context.MODE_PRIVATE)
        val cartString = sharedPref.getString("cart_list_string", "") ?: ""
        val items = if (cartString.isEmpty()) emptyList<String>() else cartString.split("###")
        val count = items.size

        if (count > 0) {
            tvCartBadge.visibility = View.VISIBLE
            tvCartBadge.text = if (count > 9) "9+" else count.toString()
        } else {
            tvCartBadge.visibility = View.GONE
        }
    }

    private fun setupCartLogic() {
        val sharedPref = getSharedPreferences("PerfumePrefs", Context.MODE_PRIVATE)
        val buttons = listOf(
            R.id.btnCart1, R.id.btnCart2, R.id.btnCart3,
            R.id.btnCart4, R.id.btnCart5, R.id.btnCart6
        )

        buttons.forEachIndexed { index, buttonId ->
            findViewById<Button>(buttonId)?.setOnClickListener {
                if (index < currentMoodPerfumes.size) {
                    val selected = currentMoodPerfumes[index]
                    val entry = "${selected.name}|${selected.imageRes}|${selected.price}"
                    var cartString = sharedPref.getString("cart_list_string", "") ?: ""

                    cartString = if (cartString.isEmpty()) entry else "$cartString###$entry"
                    sharedPref.edit().putString("cart_list_string", cartString).apply()

                    refreshBadge()
                    Toast.makeText(this, "${selected.name} added to Bag!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updatePerfumeContent(mood: String) {
        val imgViews = listOf(
            findViewById<ImageView>(R.id.imgPerfume1), findViewById<ImageView>(R.id.imgPerfume2),
            findViewById<ImageView>(R.id.imgPerfume3), findViewById<ImageView>(R.id.imgPerfume4),
            findViewById<ImageView>(R.id.imgPerfume5), findViewById<ImageView>(R.id.imgPerfume6)
        )
        val nameViews = listOf(
            findViewById<TextView>(R.id.name1), findViewById<TextView>(R.id.name2),
            findViewById<TextView>(R.id.name3), findViewById<TextView>(R.id.name4),
            findViewById<TextView>(R.id.name5), findViewById<TextView>(R.id.name6)
        )
        val priceViews = listOf(
            findViewById<TextView>(R.id.price1), findViewById<TextView>(R.id.price2),
            findViewById<TextView>(R.id.price3), findViewById<TextView>(R.id.price4),
            findViewById<TextView>(R.id.price5), findViewById<TextView>(R.id.price6)
        )

        val risqueFont = ResourcesCompat.getFont(this, R.font.risque)
        currentMoodPerfumes.clear()

        val perfumes = when (mood.lowercase()) {
            "confidence" -> listOf(
                PerfumeData("Byredo Mojave", R.drawable.perfume_1, 850),
                PerfumeData("Coco Mademoiselle", R.drawable.perfume_2, 620),
                PerfumeData("My Way", R.drawable.my_way, 450),
                PerfumeData("Justmylook", R.drawable.perfume_4, 320),
                PerfumeData("Libre", R.drawable.perfume_5, 580),
                PerfumeData("Scarlet Poppy", R.drawable.perfume_6, 740)
            )
            "energetic" -> listOf(
                PerfumeData("Armani", R.drawable.armani, 410),
                PerfumeData("Victoria", R.drawable.victoria, 290),
                PerfumeData("Green Yulong", R.drawable.green_yulong, 820),
                PerfumeData("Black YSL", R.drawable.black_ysl, 550),
                PerfumeData("Victoria Secret", R.drawable.victoria_secret, 310),
                PerfumeData("Yellow", R.drawable.yellow, 380)
            )
            "happy" -> listOf(
                PerfumeData("Gucci Flora", R.drawable.gucci_flora, 520),
                PerfumeData("Chanel", R.drawable.chanel, 690),
                PerfumeData("Givenchy", R.drawable.givenchy, 480),
                PerfumeData("Miss Dior", R.drawable.miss_dior, 590),
                PerfumeData("Pink Star", R.drawable.pink_star, 240),
                PerfumeData("Sundazed", R.drawable.sundazed, 780)
            )
            else -> listOf(
                PerfumeData("Yulong", R.drawable.yulong_perfume, 820),
                PerfumeData("Perfume That", R.drawable.perfume_that, 150),
                PerfumeData("French", R.drawable.french, 420),
                PerfumeData("Milky Musk", R.drawable.milky_musk, 600),
                PerfumeData("Sacred Wood", R.drawable.kilian_sacred, 950),
                PerfumeData("Red Rose", R.drawable.red_rose, 340)
            )
        }
        currentMoodPerfumes.addAll(perfumes)

        currentMoodPerfumes.forEachIndexed { i, data ->
            if (i < imgViews.size) {
                imgViews[i].setImageResource(data.imageRes)
                nameViews[i].text = data.name
                nameViews[i].typeface = risqueFont
                priceViews[i].text = "${data.price} AED"
                priceViews[i].typeface = Typeface.DEFAULT_BOLD
            }
        }
    }

    private fun setupAnimations() {
        val cards = listOf(
            findViewById<CardView>(R.id.box1), findViewById<CardView>(R.id.box2),
            findViewById<CardView>(R.id.card3), findViewById<CardView>(R.id.card4),
            findViewById<CardView>(R.id.card5), findViewById<CardView>(R.id.card6)
        )
        val perfumes = listOf(
            findViewById<ImageView>(R.id.imgPerfume1), findViewById<ImageView>(R.id.imgPerfume2),
            findViewById<ImageView>(R.id.imgPerfume3), findViewById<ImageView>(R.id.imgPerfume4),
            findViewById<ImageView>(R.id.imgPerfume5), findViewById<ImageView>(R.id.imgPerfume6)
        )

        cards.forEachIndexed { i, card ->
            card.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(180L * i)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }

        perfumes.forEach { startBreathingAnimation(it) }
        animateFooterQuotes()
    }

    private fun startBreathingAnimation(view: View) {
        val sX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.06f)
        val sY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.06f)
        val a = ObjectAnimator.ofFloat(view, "alpha", 0.85f, 1f)

        AnimatorSet().apply {
            playTogether(sX, sY, a)
            duration = 2500 + (Math.random() * 800).toLong()
            interpolator = AccelerateDecelerateInterpolator()
            listOf(sX, sY, a).forEach {
                it.repeatCount = ValueAnimator.INFINITE
                it.repeatMode = ValueAnimator.REVERSE
            }
            start()
        }
    }

    private fun animateFooterQuotes() {
        val q = findViewById<TextView>(R.id.tvFooterQuote)
        val b = findViewById<TextView>(R.id.tvFooterBranding)
        val d = findViewById<View>(R.id.footer_divider)

        val risqueFont = ResourcesCompat.getFont(this, R.font.risque)
        q?.typeface = risqueFont
        b?.typeface = risqueFont

        listOf(q, b, d).forEach { it?.alpha = 0f }

        d?.animate()?.alpha(1f)?.setDuration(1000)?.setStartDelay(1400)?.start()
        q?.animate()?.alpha(0.8f)?.setDuration(1000)?.setStartDelay(1600)?.start()
        b?.animate()?.alpha(1f)?.setDuration(1000)?.setStartDelay(1800)?.start()
    }
}
