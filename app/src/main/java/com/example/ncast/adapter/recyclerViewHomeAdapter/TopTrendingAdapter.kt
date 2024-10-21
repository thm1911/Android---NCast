package com.example.ncast.adapter.recyclerViewHomeAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ncast.R

class TopTrendingAdapter :
    RecyclerView.Adapter<TopTrendingAdapter.TopTrendingViewHolder>() {

    inner class TopTrendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.music_text_top_trend)
        val image: ImageView = itemView.findViewById(R.id.music_picture_top_trend)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopTrendingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trend_music, parent, false)
        return TopTrendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopTrendingViewHolder, position: Int) {
        holder.title.text = "Pop Mix $position"
        holder.image.setImageResource(R.drawable.top_trending)
    }

    override fun getItemCount(): Int = 4
}