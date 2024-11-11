package com.example.ncast.adapter.recycleViewAdapterLibrary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ncast.R
import com.example.ncast.model.track.TrackResponse

class FavoriteTrackAdapter(
    private var trackList: List<TrackResponse>,
    private val onTrackClick: (TrackResponse) -> Unit
) : RecyclerView.Adapter<FavoriteTrackAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.item_image)
        val itemTitle: TextView = view.findViewById(R.id.item_title)
        val itemArtist: TextView = view.findViewById(R.id.item_artist)

        init {
            view.setOnClickListener {
                val track = trackList[adapterPosition]
                onTrackClick(track)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_song, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]

        holder.itemTitle.text = track.name
        holder.itemArtist.text = track.artists.joinToString(", ") { it.name }
        Glide.with(holder.itemView.context)
            .load(track.album.images.firstOrNull()?.url)
            .placeholder(R.drawable.picture_album)
            .into(holder.itemImage)
    }

    override fun getItemCount(): Int = trackList.size

    fun updateData(newTrackList: List<TrackResponse>) {
        trackList = newTrackList
        notifyDataSetChanged()
    }
}
