package com.example.ncast.adapter.recycleViewAdapterSearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ncast.R
import com.example.ncast.databinding.ItemDiscoveriesSearchBinding

class DiscoveriesAdapter:RecyclerView.Adapter<DiscoveriesAdapter.DiscoveriesHolder>() {

    inner class DiscoveriesHolder(val binding: ItemDiscoveriesSearchBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoveriesHolder {
        val binding=ItemDiscoveriesSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DiscoveriesHolder(binding)
    }

    override fun getItemCount(): Int =4

    override fun onBindViewHolder(holder: DiscoveriesHolder, position: Int) {
        holder.binding.namePopularAlbums.text="Podcasts"
        holder.binding.itemImage.setImageResource(R.drawable.trend_song)
    }
}