package com.thumb.nail

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.ui.PlayerView

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView

    private lateinit var fpsText: TextView
    private lateinit var bitrateText: TextView
    private lateinit var resolutionText: TextView
    private lateinit var pingText: TextView
    private lateinit var packetLossText: TextView
    private lateinit var fullscreenButton: ImageButton

    private val handler = Handler(Looper.getMainLooper())
    private var droppedFrames = 0

    private val updateStats = object : Runnable {
        @OptIn(UnstableApi::class)
        override fun run() {
            val videoFormat = player.videoFormat
            val bitrate = videoFormat?.bitrate ?: 0
            val width = videoFormat?.width ?: 0
            val height = videoFormat?.height ?: 0
            val frameRate = videoFormat?.frameRate ?: 0f

            fpsText.text = "FPS: ${"%.1f".format(frameRate)}"
            bitrateText.text = "Bitrate: ${bitrate / 1000} kbps"
            resolutionText.text = "Resolution: ${width}x$height"
            packetLossText.text = "Packet Loss: $droppedFrames frames"

            handler.postDelayed(this, 1000)
        }
    }

    private var isFullscreen = false

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Views
        playerView = findViewById(R.id.playerView)
        fpsText = findViewById(R.id.fpsText)
        bitrateText = findViewById(R.id.bitrateText)
        resolutionText = findViewById(R.id.resolutionText)
        pingText = findViewById(R.id.pingText)
        packetLossText = findViewById(R.id.packetLossText)
        fullscreenButton = findViewById(R.id.fullscreenButton)

        // Init player
        val videoResId = intent.getIntExtra(EXTRA_RAW_VIDEO_ID, -1)
        if (videoResId == -1) {
            finish()
            return
        }

        player = ExoPlayer.Builder(this).build().also {
            playerView.player = it
            val uri = Uri.parse("android.resource://$packageName/$videoResId")
            val mediaItem = MediaItem.fromUri(uri)
            it.setMediaItem(mediaItem)
            it.prepare()
            it.play()
        }

        // Analytics listener for dropped frames
        player.addAnalyticsListener(object : AnalyticsListener {
            override fun onDroppedVideoFrames(
                eventTime: AnalyticsListener.EventTime,
                droppedFramesCount: Int,
                elapsedMs: Long
            ) {
                droppedFrames += droppedFramesCount
            }
        })

        handler.post(updateStats)

        fullscreenButton.setOnClickListener {
            toggleFullscreen()
        }

        measurePing()
    }

    private fun toggleFullscreen() {
        requestedOrientation = if (isFullscreen) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        isFullscreen = !isFullscreen
    }

    private fun measurePing(host: String = "google.com") {
        Thread {
            val start = System.currentTimeMillis()
            try {
                val process = Runtime.getRuntime().exec("/system/bin/ping -c 1 $host")
                val result = process.waitFor()
                val end = System.currentTimeMillis()
                val pingTime = if (result == 0) end - start else -1
                runOnUiThread {
                    pingText.text = "Ping: ${if (pingTime >= 0) "$pingTime ms" else "Failed"}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    pingText.text = "Ping: Error"
                }
            }
        }.start()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updateStats)
        player.release()
    }

    companion object {
        const val EXTRA_RAW_VIDEO_ID = "RAW_VIDEO_ID"
    }
}
