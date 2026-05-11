package com.example.perfumebar

import android.content.Intent
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity(), TextureView.SurfaceTextureListener {

    private lateinit var textureView: TextureView
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide app bar for full screen splash video
        supportActionBar?.hide()

        setContentView(R.layout.activity_splash)

        textureView = findViewById(R.id.textureViewSplash)
        textureView.surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        val videoPath = "android.resource://$packageName/${R.raw.splash_video}"
        val uri = Uri.parse(videoPath)

        mediaPlayer = MediaPlayer().apply {
            setDataSource(applicationContext, uri)
            setSurface(Surface(surface))
            isLooping = false
            prepareAsync()

            setOnPreparedListener { mp ->
                val videoWidth = mp.videoWidth.toFloat()
                val videoHeight = mp.videoHeight.toFloat()
                val viewWidth = textureView.width.toFloat()
                val viewHeight = textureView.height.toFloat()

                val scaleX: Float
                val scaleY: Float
                val dx: Float
                val dy: Float

                if (videoWidth * viewHeight > viewWidth * videoHeight) {
                    scaleY = viewHeight / videoHeight
                    scaleX = scaleY
                    dx = (viewWidth - videoWidth * scaleX) / 2f
                    dy = 0f
                } else {
                    scaleX = viewWidth / videoWidth
                    scaleY = scaleX
                    dx = 0f
                    dy = (viewHeight - videoHeight * scaleY) / 2f
                }

                val matrix = Matrix()
                matrix.setScale(scaleX, scaleY)
                matrix.postTranslate(dx, dy)
                textureView.setTransform(matrix)

                mp.start()
            }

            setOnCompletionListener {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        mediaPlayer?.release()
        mediaPlayer = null
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}