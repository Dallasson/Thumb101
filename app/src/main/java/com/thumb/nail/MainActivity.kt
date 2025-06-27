package com.thumb.nail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.GridView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {


    private val videoList = listOf(
        VideoItem(R.drawable.thumb_1,
            "https://firebasestorage.googleapis.com/v0/b/sushi-6533d.appspot.com/o/videos%2Fvideo_720.mp4?alt=media&token=3fa4bd79-4990-4a72-979a-b16521c4c29e","Video 720p"),
        VideoItem(R.drawable.thumb_2,
            "https://firebasestorage.googleapis.com/v0/b/sushi-6533d.appspot.com/o/videos%2Fvideo_1080.mp4?alt=media&token=410e619c-2cc2-469f-92e3-cf5c384ed604","Video 1080p"),
        VideoItem(R.drawable.thumb_4,
            "https://firebasestorage.googleapis.com/v0/b/sushi-6533d.appspot.com/o/videos%2Fvideo_4k.mp4?alt=media&token=a35af471-aba3-4ed0-aeeb-e420c167fcc9","Video 4k"),
        VideoItem(R.drawable.thumb_3,
            "https://firebasestorage.googleapis.com/v0/b/sushi-6533d.appspot.com/o/videos%2Fvecteezy_8k-air-pollution-in-the-city-at-sunset-in-winter_7689827.mov?alt=media&token=574b9aa0-9874-472d-8e64-2eac16e7eeba","Video 8K"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val rv = findViewById<RecyclerView>(R.id.rv)
        val layoutManager = GridLayoutManager(this,2)

        val adapter = VideoAdapter(this, videoList)
        rv.layoutManager = layoutManager
        rv.adapter = adapter

    }
}