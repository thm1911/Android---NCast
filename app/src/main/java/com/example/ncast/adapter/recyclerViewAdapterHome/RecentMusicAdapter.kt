package com.example.ncast.adapter.recyclerViewAdapterHome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ncast.R
import com.example.ncast.model.track.TrackResponse

class RecentMusicAdapter(
    private var trackList: List<TrackResponse>,
    private val onTrackClick: (TrackResponse) -> Unit
) : RecyclerView.Adapter<RecentMusicAdapter.RecentMusicViewHolder>() {

    inner class RecentMusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.item_title)
        val artist: TextView = itemView.findViewById(R.id.item_artist)
        val image: ImageView = itemView.findViewById(R.id.item_image)

        init {
            itemView.setOnClickListener {
                val track = trackList[adapterPosition]
                onTrackClick(track)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentMusicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_song_home, parent, false)
        return RecentMusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentMusicViewHolder, position: Int) {
        val track = trackList[position]
        holder.title.text = track.name
        holder.artist.text = track.artists.joinToString(", ") { it.name }

        if (track.album.images.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(track.album.images[0].url)
                .placeholder(R.drawable.picture_album)
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.picture_album)
        }
    }

    override fun getItemCount(): Int = trackList.size

    fun setData(newTrackList: List<TrackResponse>) {
        val diffCallback = TrackDiffUtilCallback(trackList, newTrackList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        trackList = newTrackList
        diffResult.dispatchUpdatesTo(this)
    }

    class TrackDiffUtilCallback(
        private val oldList: List<TrackResponse>,
        private val newList: List<TrackResponse>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
