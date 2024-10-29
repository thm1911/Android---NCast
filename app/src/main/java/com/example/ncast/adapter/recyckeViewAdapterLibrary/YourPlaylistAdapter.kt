package com.example.ncast.adapter.recyckeViewAdapterLibrary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ncast.R
import com.example.ncast.databinding.ItemMyPlaylistLibraryBinding

class YourPlaylistAdapter:RecyclerView.Adapter<YourPlaylistAdapter.YourPlaylistViewHolder>() {
    inner class YourPlaylistViewHolder(val binding:ItemMyPlaylistLibraryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourPlaylistViewHolder {
        val binding = ItemMyPlaylistLibraryBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false
        )
        return YourPlaylistViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 20
    }

    override fun onBindViewHolder(holder: YourPlaylistViewHolder, position: Int) {
        holder.binding.itemTitle.text = "For the road"
        holder.binding.itemAuthorName.text = "Davido"
        holder.binding.itemImage.setImageResource(R.drawable.trend_song)
    }

}
