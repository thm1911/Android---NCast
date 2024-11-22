package com.example.ncast.adapter.recyclerViewAdapterHome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ncast.model.newAlbumRelease.AlbumResponse
import com.example.ncast.databinding.ItemTrackInAlbumBinding

class AlbumTrackAdapter(
    private var songList: MutableList<AlbumResponse.Item>,
    private val onClick: (AlbumResponse.Item) -> Unit
) : RecyclerView.Adapter<AlbumTrackAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(private val binding: ItemTrackInAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val stt = binding.stt
        val nameTrack = binding.nameSong
        val artist = binding.artist

        fun bind(track: AlbumResponse.Item, position: Int) {
            stt.setText(position.toString())
            nameTrack.setText(track.name)
            val artistNames = track.artists.joinToString(", ") { it.name }
            binding.artist.text = artistNames
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding =
            ItemTrackInAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val song = songList[position]
        holder.bind(song, position + 1)
        holder.itemView.setOnClickListener {
            onClick(song)
        }
    }

    fun setData(songList: List<AlbumResponse.Item>) {
        val result = DiffUtil.calculateDiff(SongDiffUtilCallBack(this.songList, songList))
        this.songList.clear()
        this.songList.addAll(songList)
        result.dispatchUpdatesTo(this)

    }

    class SongDiffUtilCallBack(
        private val songOldList: List<AlbumResponse.Item>,
        private val songNewList: List<AlbumResponse.Item>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return songOldList.size
        }

        override fun getNewListSize(): Int {
            return songNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return songOldList[oldItemPosition].id == songNewList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return songOldList[oldItemPosition] == songNewList[newItemPosition]
        }

    }
}