package com.example.ncast.adapter.recyclerViewAdapterHome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.ncast.R

class RecentMusicListeningAdapter :
    RecyclerView.Adapter<RecentMusicListeningAdapter.RecentMusicViewHolder>() {

    inner class RecentMusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.music_image_recent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentMusicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_music_listening_home, parent, false)
        return RecentMusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentMusicViewHolder, position: Int) {
        holder.image.setImageResource(R.drawable.pic_trend)
    }

    override fun getItemCount(): Int = 4
}