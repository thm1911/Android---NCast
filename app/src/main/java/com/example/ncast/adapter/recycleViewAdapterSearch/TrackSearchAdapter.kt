package com.example.ncast.adapter.recycleViewAdapterSearch

import Tracks
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ncast.databinding.ItemTrackInAlbumBinding
import com.example.ncast.databinding.ItemTrackInAlbumPicBinding

class TrackSearchAdapter(
    private val trackList: MutableList<Tracks.Item>,
    private val onClick: (Tracks.Item) -> Unit
) :
    RecyclerView.Adapter<TrackSearchAdapter.TrackSearchViewHolder>() {

    inner class TrackSearchViewHolder(val binding: ItemTrackInAlbumPicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(track: Tracks.Item) {
            binding.stt.setText("")
            binding.nameSong.setText(track.name)
            val artistNames = track.artists.joinToString(", ") { it.name }
            binding.artist.text = artistNames

            val imageUrl = track.album.images[0].url
            Glide.with(binding.itemImage.context)
                .load(imageUrl)
                .into(binding.itemImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackSearchViewHolder {
        val binding =
            ItemTrackInAlbumPicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackSearchViewHolder(binding)
    }

    override fun getItemCount(): Int = trackList.size

    override fun onBindViewHolder(holder: TrackSearchViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onClick(track)
        }
    }

    fun setData(trackList: List<Tracks.Item>) {
        val ressult = DiffUtil.calculateDiff(SearchTrackDillUtil(this.trackList, trackList))
        this.trackList.clear()
        this.trackList.addAll(trackList)
        ressult.dispatchUpdatesTo(this)
    }

    class SearchTrackDillUtil(
        private val oldList: List<Tracks.Item>,
        private val newList: List<Tracks.Item>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }

}