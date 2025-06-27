package com.thumb.nail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView



class VideoAdapter(private val context: Context, private val videos: List<VideoItem>) : BaseAdapter() {
    override fun getCount() = videos.size
    override fun getItem(position: Int) = videos[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.grid_item_video, parent, false)

        val thumbnail = view.findViewById<ImageView>(R.id.videoThumbnail)
        val label = view.findViewById<TextView>(R.id.videoLabel)

        thumbnail.setImageResource(videos[position].thumbnailResId)
        label.text = videos[position].videoType

        return view
    }
}
