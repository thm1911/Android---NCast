package com.example.ncast.adapter.recyclerViewAdapterHome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ncast.model.newAlbumRelease.AlbumResponse
import com.example.ncast.databinding.ItemSongInAlbumBinding

class SongAdapter(
    private var songList: MutableList<AlbumResponse.Item>,
    private val onClick: (AlbumResponse.Item) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(private val binding: ItemSongInAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val stt = binding.stt
        val nameSong = binding.nameSong
        val artist = binding.artist

        fun bind(song: AlbumResponse.Item, position: Int) {
            stt.setText(position.toString())
            nameSong.setText(song.name)
            artist.setText(song.artists.get(0).name)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding =
            ItemSongInAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songList[position]
        holder.bind(song, position + 1)
        holder.itemView.setOnClickListener {
            onClick(song)
        }
    }

    fun setData(songList: List<AlbumResponse.Item>){
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