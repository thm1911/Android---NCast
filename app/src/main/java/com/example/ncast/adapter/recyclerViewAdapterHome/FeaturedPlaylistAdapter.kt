package com.example.ncast.adapter.recyclerViewAdapterHome

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ncast.databinding.ItemFeaturedPlaylistHomeBinding
import com.example.ncast.model.featuredPlaylist.FeaturedPlaylist

class FeaturedPlaylistAdapter(
    private val featuredPlaylists: MutableList<FeaturedPlaylist.Item>,
    private val onCLick: (FeaturedPlaylist.Item) -> Unit
) :
    RecyclerView.Adapter<FeaturedPlaylistAdapter.FeaturedPlaylistViewHolder>() {
    private var showAll: Boolean = false

    inner class FeaturedPlaylistViewHolder(binding: ItemFeaturedPlaylistHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val image = binding.image

        fun bind(playlist: FeaturedPlaylist.Item) {
            Glide.with(image.context)
                .load(playlist.images.get(0).url)
                .into(image)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeaturedPlaylistViewHolder {
        val binding = ItemFeaturedPlaylistHomeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FeaturedPlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeaturedPlaylistViewHolder, position: Int) {
        val playlist = featuredPlaylists[position]
        holder.bind(playlist)
        holder.image.setOnClickListener {
            onCLick(playlist)
        }
    }

    fun showAllPlaylist(showAll: Boolean) {
        this.showAll = showAll
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if (showAll) return featuredPlaylists.size
        else return minOf(4, featuredPlaylists.size)
    }

    fun setData(featuredPlaylists: List<FeaturedPlaylist.Item>) {
        val result = DiffUtil.calculateDiff(
            PlaylistDiffUtilCallBack(
                this.featuredPlaylists,
                featuredPlaylists
            )
        )
        this.featuredPlaylists.clear()
        this.featuredPlaylists.addAll(featuredPlaylists)
        result.dispatchUpdatesTo(this)
    }

    class PlaylistDiffUtilCallBack(
        private val oldPlaylists: List<FeaturedPlaylist.Item>,
        private val newPlaylists: List<FeaturedPlaylist.Item>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldPlaylists.size
        }

        override fun getNewListSize(): Int {
            return newPlaylists.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldPlaylists[oldItemPosition].id == newPlaylists[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldPlaylists[oldItemPosition] == newPlaylists[newItemPosition]
        }

    }
}