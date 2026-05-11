package com.example.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge

class MainActivity : AppCompatActivity() {

    private var isMixerStage = false

    //  Stores selected fruit flavor
    private var pickedFruit: String = ""

    //  Stores selected mixer
    private var pickedMixer: String = ""

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)  // Links this activity to the XML layout


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


// Landing page and main screen layouts
        val layoutStartPage = findViewById<View>(R.id.landingPage)
        val layoutMainApp = findViewById<View>(R.id.mainLayout)
        val btnStartApp = findViewById<RelativeLayout>(R.id.btnStart)

        val  txtInstruction = findViewById<TextView>(R.id. txtInstruction)
        val btnNext = findViewById<RelativeLayout>(R.id.btnNext)
        val txtNext = findViewById<TextView>(R.id.txtNext)

        val btnInstruction = findViewById<ImageView>(R.id.btnInstruction)

//  Makes the background dimmer when instructions pop up on the screen
        val viewDimmer = findViewById<View>(R.id.overlayEffect)
        val imgInstructionOverlay = findViewById<ImageView>(R.id.InstructionsBgImage)
        val layoutInstructionText = findViewById<View>(R.id.InstructionTxt)

        val layoutResultOverlay = findViewById<FrameLayout>(R.id.resultPage)
        val txtResultName = findViewById<TextView>(R.id.mocktailName)
        val txtResultDescription = findViewById<TextView>(R.id.mocktailDescription)
        val imgAnimatedDrink = findViewById<ImageView>(R.id.mocktailPicture)
        val btnCloseResult = findViewById<ImageView>(R.id.btnClose)

        val fruitFlavorOptions = listOf(
            findViewById<FrameLayout>(R.id.flavorMango),
            findViewById<FrameLayout>(R.id.flavorStrawberry),
            findViewById<FrameLayout>(R.id.flavorWatermelon),
            findViewById<FrameLayout>(R.id.flavorOrange)
        )

        val mixerOptions = listOf(
            findViewById<FrameLayout>(R.id.mixerLemonSoda),
            findViewById<FrameLayout>(R.id.mixerSparklingWater),
            findViewById<FrameLayout>(R.id.mixerCoconutWater),
            findViewById<FrameLayout>(R.id.mixerGingerLemon)
        )

        val fruitOverlays = listOf(
            findViewById<ImageView>(R.id.choose_mango),
            findViewById<ImageView>(R.id.choose_strawberry),
            findViewById<ImageView>(R.id.choose_watermelon),
            findViewById<ImageView>(R.id.choose_orange)
        )

        val mixerOverlays = listOf(
            findViewById<ImageView>(R.id.choose_lemon),
            findViewById<ImageView>(R.id.choose_sparklingWater),
            findViewById<ImageView>(R.id.choose_coconutWater),
            findViewById<ImageView>(R.id.choose_gingerLemon)
        )


        btnStartApp.setOnClickListener {
            layoutStartPage.visibility = View.GONE
            layoutMainApp.visibility = View.VISIBLE
        }


        btnInstruction.setOnClickListener {
            viewDimmer.visibility = View.VISIBLE
            imgInstructionOverlay.visibility = View.VISIBLE
            layoutInstructionText?.visibility = View.VISIBLE
        }

        val closeInfoListener = View.OnClickListener {
            imgInstructionOverlay.visibility = View.GONE
            layoutInstructionText?.visibility = View.GONE
            viewDimmer.visibility = View.GONE
        }

        imgInstructionOverlay.setOnClickListener(closeInfoListener)
        viewDimmer.setOnClickListener {
            if (imgInstructionOverlay.visibility == View.VISIBLE) {
                imgInstructionOverlay.visibility = View.GONE
                layoutInstructionText?.visibility = View.GONE
                viewDimmer.visibility = View.GONE
            }
        }

// Handles user selection and saves the selected option in the pickedFruit variable when clicked
        fruitFlavorOptions.forEachIndexed { index, card ->
            card?.setOnClickListener {
                pickedFruit = when (card.id) {
                    R.id.flavorMango -> "Mango"
                    R.id.flavorStrawberry -> "Strawberry"
                    R.id.flavorWatermelon -> "Watermelon"
                    else -> "Orange"
                }

// Display a toast message to inform the user about their selected option
                val toast = Toast.makeText(this, "$pickedFruit selected!", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.TOP, 0, 300)
                toast.show()

                applySelectionEffect(card, fruitFlavorOptions, fruitOverlays, index)
                showNextButton(btnNext)
            }
        }

 // Handles user selection and saves the selected option in the pickedMixer variable when clicked
        mixerOptions.forEachIndexed { index, card ->
            card?.setOnClickListener {
                pickedMixer = when (card.id) {
                    R.id.mixerLemonSoda -> "Lemon Soda"
                    R.id.mixerSparklingWater -> "Sparkling Water"
                    R.id.mixerCoconutWater -> "Coconut Water"
                    else -> "Ginger Lemon"
                }


                val toast = Toast.makeText(this, "Added $pickedMixer to the mix!", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.TOP, 0, 300)
                toast.show()

                applySelectionEffect(card, mixerOptions, mixerOverlays, index)
                showNextButton(btnNext)
            }
        }

// Switches from flavor selection step to mixer selection step
        btnNext.setOnClickListener {
            if (!isMixerStage) {
                isMixerStage = true
                txtInstruction.text = "Pick a Mixer"
                txtNext.text = "Mix"
                fruitFlavorOptions.forEach { it?.visibility = View.GONE }
                mixerOptions.forEach { it?.visibility = View.VISIBLE }
                btnNext.visibility = View.INVISIBLE
            } else {
                val drinkName = getDrinkName(pickedFruit, pickedMixer)
                val description = getDrinkDescription(pickedFruit, pickedMixer)

// Converts the mocktail name into a valid filename that matches the image in the drawable folder
                val fileName = drinkName.lowercase().replace(" ", "_").replace("&", "and")

                txtResultName.text = drinkName
                txtResultDescription.text = description

                val resId = resources.getIdentifier(fileName, "drawable", packageName)
                if (resId != 0) {
                    imgAnimatedDrink.setImageResource(resId)
                }

                viewDimmer.visibility = View.VISIBLE
                layoutResultOverlay.visibility = View.VISIBLE
                layoutResultOverlay.alpha = 0f
                layoutResultOverlay.animate().alpha(1f).setDuration(300).start()
            }
        }

//Resets user selections when result screen is closed to enable new combination
        val resetListener = View.OnClickListener {
            layoutResultOverlay.visibility = View.GONE
            viewDimmer.visibility = View.GONE
            isMixerStage = false
            pickedFruit = ""
            pickedMixer = ""
            txtInstruction.text = "Pick a Flavor"
            txtNext.text = "Next"
            btnNext.visibility = View.INVISIBLE
            fruitOverlays.forEach { it?.visibility = View.INVISIBLE }
            mixerOverlays.forEach { it?.visibility = View.INVISIBLE }
            fruitFlavorOptions.forEach {
                it?.visibility = View.VISIBLE
                it?.alpha = 1.0f
                it?.scaleX = 1.0f
                it?.scaleY = 1.0f
            }
            mixerOptions.forEach {
                it?.visibility = View.GONE
                it?.alpha = 1.0f
                it?.scaleX = 1.0f
                it?.scaleY = 1.0f
            }
        }

        layoutResultOverlay.setOnClickListener(resetListener)
        btnCloseResult?.setOnClickListener(resetListener)
    }


// Display mocktail name based on the selected fruit flavor and mixer
    private fun getDrinkName(fruit: String, mixer: String): String {
        return when (fruit) {
            "Strawberry" -> when (mixer) {
                "Lemon Soda" -> "Strawberry Sparkler"
                "Coconut Water" -> "Pink Island Dream"
                "Sparkling Water" -> "Ruby Essence"
                else -> "Zesty Berry"
            }
            "Mango" -> when (mixer) {
                "Lemon Soda" -> "Mango Tango"
                "Coconut Water" -> "Tropical Oasis"
                "Sparkling Water" -> "Golden Mist"
                else -> "Sunset Spice"
            }
            "Watermelon" -> when (mixer) {
                "Lemon Soda" -> "Melon Pop"
                "Coconut Water" -> "The Hydrator"
                "Sparkling Water" -> "Summer Breeze"
                else -> "Fire & Ice"
            }
            else -> when (mixer) {
                "Lemon Soda" -> "Citrus Blast"
                "Coconut Water" -> "Orange Grove"
                "Sparkling Water" -> "Pure Sunrise"
                else -> "Immunity Booster"
            }
        }
    }

//  Display mocktail description based on the selected combination
    private fun getDrinkDescription(fruit: String, mixer: String): String {
        return when (fruit) {
            "Strawberry" -> when (mixer) {
                "Lemon Soda" -> "A bubbly, sweet cherry delight that dances on your mouth."
                "Coconut Water" -> "Creamy coconut pairs with juicy strawberries for a tropical vacation."
                "Sparkling Water" -> "Light and purely refreshing berry-infused water."
                else -> "A bold punch of ginger combined with sweet summer strawberries."
            }
            "Mango" -> when (mixer) {
                "Lemon Soda" -> "The perfect party mix: tropical mango with a citrus flavor."
                "Coconut Water" -> "Hydrating and rich with golden sun-ripened flavor.."
                "Sparkling Water" -> "A delicate and refreshing way to enjoy the King of Fruits.."
                else -> "Warm ginger flavors balance the sweetness of the exotic mango.\n."
            }
            "Watermelon" -> when (mixer) {
                "Lemon Soda" -> "Like liquid candy! Very refreshing and enjoyable on a hot day."
                "Coconut Water" -> "The most delicious ultimate thirst quencher with deep hydration and fresh melon taste."
                "Sparkling Water" -> "Refined and delicious. A delicate summer requirement"
                else -> "An experimental blend of chilled watermelon and hot ginger lemon."
            }
            else -> when (mixer) {
                "Lemon Soda" -> "An explosion of vitamin C with double the citrus bubbles!"
                "Coconut Water" -> "A unique blend that tastes like a sunset on a hidden beach."
                "Sparkling Water" -> "Healthy, and refreshing mocktail. The best way to start your day."
                else -> "Powerful citrus and ginger mix to reactivate your senses."
            }
        }
    }

    private fun applySelectionEffect(selected: View, allItems: List<FrameLayout?>, overlays: List<ImageView?>, currentIndex: Int) {
        allItems.forEachIndexed { index, item ->
            if (item == selected) {
                item?.animate()?.scaleX(1.05f)?.scaleY(1.05f)?.alpha(1.0f)?.setDuration(200)?.start()
                overlays[index]?.visibility = View.VISIBLE
            } else {
                item?.animate()?.scaleX(0.9f)?.scaleY(0.9f)?.alpha(0.4f)?.setDuration(200)?.start()
                overlays[index]?.visibility = View.INVISIBLE
            }
        }
    }

    private fun showNextButton(btnNext: View) {
        if (btnNext.visibility != View.VISIBLE) {
            btnNext.visibility = View.VISIBLE
            btnNext.alpha = 0f
            btnNext.animate().alpha(1f).setDuration(300).start()
        }
    }
}