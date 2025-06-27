package com.thumb.nail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.GridView


class MainActivity : AppCompatActivity() {
    private val videoList = listOf(
        VideoItem(R.drawable.thumb_1, R.raw.video_720,"Video 720p"),
        VideoItem(R.drawable.thumb_2, R.raw.video_1080,"Video 1080p"),
        VideoItem(R.drawable.thumb_3, R.raw.video_2k,"Video 2K"),
        VideoItem(R.drawable.thumb_4, R.raw.video_4k,"Video 4k")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.videoGrid)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val gridView = findViewById<GridView>(R.id.videoGrid)
        val adapter = VideoAdapter(this, videoList)
        gridView.adapter = adapter

        gridView.setOnItemClickListener { _, _, position, _ ->
            val videoResId = videoList[position].rawVideoResId
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("RAW_VIDEO_ID", videoResId)
            startActivity(intent)
        }
    }
}