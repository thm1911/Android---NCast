package com.example.ncast.adapter.recycleViewAdapterSearch

import android.telephony.ims.ImsMmTelManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ncast.R
import com.example.ncast.databinding.ItemMusicSearchBinding

class UpdateEveryHourAdapter:RecyclerView.Adapter<UpdateEveryHourAdapter.UpdateSongViewHolder>() {

    inner class UpdateSongViewHolder(val binding: ItemMusicSearchBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateSongViewHolder {
        val binding = ItemMusicSearchBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false
        )
        return UpdateSongViewHolder(binding)
    }

    override fun getItemCount(): Int = 6

    override fun onBindViewHolder(holder: UpdateSongViewHolder, position: Int) {
        holder.binding.itemTitle.text = "For the road"
        holder.binding.itemAuthorName.text = "Davido"
        holder.binding.itemImage.setImageResource(R.drawable.trend_song)
    }
}