package com.example.ncast.adapter.recycleViewAdapterLibrary

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ncast.databinding.ItemPickPlaylistBinding
import com.example.ncast.model.yourPlaylist.YourPlaylist

class PickPlaylistAdapter(
    private val playlist: MutableList<YourPlaylist>,
    private val onClick: (YourPlaylist) -> Unit
) : RecyclerView.Adapter<PickPlaylistAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(private val binding: ItemPickPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(playlist: YourPlaylist) {
            binding.name.text = playlist.name
            binding.checkBox.isChecked = playlist.ticked
            Log.d("test", "${playlist.name} ${playlist.ticked}")

            binding.checkBox.setOnCheckedChangeListener{_, isChecked ->
                playlist.ticked = isChecked
                onClick(playlist)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding =
            ItemPickPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlist[position]
        holder.bind(playlist)
    }

    fun setData(playlist: List<YourPlaylist>) {
        val result = DiffUtil.calculateDiff(PlaylistDiffUtil(this.playlist, playlist))
        this.playlist.clear()
        this.playlist.addAll(playlist)
        result.dispatchUpdatesTo(this)
    }

    fun getAllPlaylist(): List<YourPlaylist>{
        return playlist
    }

    class PlaylistDiffUtil(
        private val oldList: List<YourPlaylist>,
        private val newList: List<YourPlaylist>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }
}