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
        VideoItem(R.drawable.thumb_1,
            "https://firebasestorage.googleapis.com/v0/b/sushi-6533d.appspot.com/o/videos%2Fvideo_720.mp4?alt=media&token=3fa4bd79-4990-4a72-979a-b16521c4c29e","Video 720p"),
        VideoItem(R.drawable.thumb_2,
            "https://firebasestorage.googleapis.com/v0/b/sushi-6533d.appspot.com/o/videos%2Fvideo_1080.mp4?alt=media&token=410e619c-2cc2-469f-92e3-cf5c384ed604","Video 1080p"),
        VideoItem(R.drawable.thumb_3,
            "https://firebasestorage.googleapis.com/v0/b/sushi-6533d.appspot.com/o/videos%2Fvideo_2k.mp4?alt=media&token=8cdd4812-6751-4d30-9f1e-18a06c44e8fe","Video 2K"),
        VideoItem(R.drawable.thumb_4,
            "https://firebasestorage.googleapis.com/v0/b/sushi-6533d.appspot.com/o/videos%2Fvideo_4k.mp4?alt=media&token=a35af471-aba3-4ed0-aeeb-e420c167fcc9","Video 4k")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
            val videoResId = videoList[position].videoUrl
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("RAW_VIDEO_URL", videoResId)
            startActivity(intent)
        }
    }
}