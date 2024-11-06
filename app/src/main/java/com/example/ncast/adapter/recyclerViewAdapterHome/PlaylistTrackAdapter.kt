package com.example.ncast.adapter.recyclerViewAdapterHome

import android.app.PendingIntent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ncast.databinding.ItemTrackInAlbumBinding
import com.example.ncast.model.featuredPlaylist.PlaylistResponse

class PlaylistTrackAdapter(
    private var trackList: MutableList<PlaylistResponse.Item>,
    private val onClick: (PlaylistResponse.Item) -> Unit
) : RecyclerView.Adapter<PlaylistTrackAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(private val binding: ItemTrackInAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(track: PlaylistResponse.Item, position: Int){
            binding.stt.setText("${position + 1}")
            binding.artist.setText(track.track.artists.get(0).name)
            binding.nameSong.setText(track.track.name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackInAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track, position)
        holder.itemView.setOnClickListener {
            onClick(track)
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    fun setData(trackList: List<PlaylistResponse.Item>) {
        val result = DiffUtil.calculateDiff(TrackDiffUtilCallBack(this.trackList, trackList))
        this.trackList.clear()
        this.trackList.addAll(trackList)
        result.dispatchUpdatesTo(this)

    }

    class TrackDiffUtilCallBack(
        private val oldList: List<PlaylistResponse.Item>,
        private val newList: List<PlaylistResponse.Item>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].track.id == newList[newItemPosition].track.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].track == newList[newItemPosition].track
        }

    }

}