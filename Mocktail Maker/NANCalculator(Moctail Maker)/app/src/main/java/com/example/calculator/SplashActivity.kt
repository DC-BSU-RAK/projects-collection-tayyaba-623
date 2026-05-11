package com.example.calculator

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

// Displays a splash screen video when the application runs
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val videoView = findViewById<VideoView>(R.id.splashVideoView)


        val path = "android.resource://" + packageName + "/" + R.raw.splash_video
        videoView.setVideoURI(Uri.parse(path))

        videoView.start()

        // Opens the main screen when the splash video ends
        videoView.setOnCompletionListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}