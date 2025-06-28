package com.thumb.nail

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.*
import android.view.Choreographer
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.Player
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
    private var frameCounter = 0
    private var isFullscreen = false
    private var lastFrameTimeNanos = 0L
    private var currentPing = "N/A"
    private var currentHost: String = "google.com"

    private val statsRunnable = object : Runnable {
        @OptIn(UnstableApi::class)
        override fun run() {
            if (player.playbackState == Player.STATE_READY && player.isPlaying) {
                val videoFormat = player.videoFormat
                val bitrate = videoFormat?.bitrate ?: 0
                val width = videoFormat?.width ?: 0
                val height = videoFormat?.height ?: 0

                val fps = frameCounter
                frameCounter = 0

                "FPS: $fps".also { fpsText.text = it }
                "Bitrate: ${bitrate / 1000} kbps".also { bitrateText.text = it }
                "Resolution: ${width}x$height".also { resolutionText.text = it }
                "Packet Loss: $droppedFrames frames".also { packetLossText.text = it }
                "Ping: $currentPing".also { pingText.text = it }
            }
            measurePing(currentHost)
            handler.postDelayed(this, 1000)
        }
    }

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_video_player)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        referenceViews()
        setUpExoPlayer()


    }

    private fun referenceViews(){

        playerView = findViewById(R.id.playerView)
        fpsText = findViewById(R.id.fpsText)
        bitrateText = findViewById(R.id.bitrateText)
        resolutionText = findViewById(R.id.resolutionText)
        pingText = findViewById(R.id.pingText)
        packetLossText = findViewById(R.id.packetLossText)
        fullscreenButton = findViewById(R.id.fullscreenButton)

    }

    @OptIn(UnstableApi::class)
    private fun setUpExoPlayer(){

        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        val firebaseUrl = intent.getStringExtra(Utils.VIDEO_URL)
        if (firebaseUrl == null) {
            finish()
            return
        }

        val videoUri = firebaseUrl.toUri()
        val mediaItem = MediaItem.fromUri(videoUri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        currentHost = videoUri.host ?: "google.com"

        player.addAnalyticsListener(object : AnalyticsListener {
            override fun onDroppedVideoFrames(
                eventTime: AnalyticsListener.EventTime,
                droppedFramesCount: Int,
                elapsedMs: Long
            ) {
                droppedFrames += droppedFramesCount
            }
        })

        handler.post(statsRunnable)
        startFpsCounter()

        fullscreenButton.setOnClickListener { toggleFullscreen() }

    }

    private fun toggleFullscreen() {
        requestedOrientation = if (isFullscreen) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        isFullscreen = !isFullscreen
    }

    private fun measurePing(host: String) {
        Thread {
            try {
                val process = Runtime.getRuntime().exec("/system/bin/ping -c 1 $host")
                val result = process.waitFor()
                val output = process.inputStream.bufferedReader().readText()
                val timeLine = output.lines().find { it.contains("time=") }
                val time = timeLine?.substringAfter("time=")?.substringBefore(" ms")?.trim()
                currentPing = if (result == 0 && time != null) "$time ms" else "Failed"
            } catch (e: Exception) {
                currentPing = "Error"
            }
        }.start()
    }

    private fun startFpsCounter() {
        Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                if (lastFrameTimeNanos > 0 && player.isPlaying) {
                    frameCounter++
                }
                lastFrameTimeNanos = frameTimeNanos
                Choreographer.getInstance().postFrameCallback(this)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(statsRunnable)
        player.release()
    }



}