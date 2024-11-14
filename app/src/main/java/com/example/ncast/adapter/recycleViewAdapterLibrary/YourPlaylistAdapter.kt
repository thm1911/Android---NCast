package com.example.ncast.adapter.recycleViewAdapterLibrary

import android.provider.Settings.Global
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ncast.R
import com.example.ncast.databinding.FragmentAddNewPlaylistBinding
import com.example.ncast.databinding.ItemMyPlaylistLibraryBinding
import com.example.ncast.model.yourPlaylist.YourPlaylist

class YourPlaylistAdapter(
    private val yourPlaylist: MutableList<YourPlaylist>,
    private val onClick: (YourPlaylist) -> Unit
) : RecyclerView.Adapter<YourPlaylistAdapter.YourPlaylistViewHolder>() {

    inner class YourPlaylistViewHolder(val binding: ItemMyPlaylistLibraryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(playlist: YourPlaylist){
            binding.itemTitle.setText(playlist.name)
            Glide.with(binding.itemImage.context)
                .load(playlist.imageUrl)
                .into(binding.itemImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourPlaylistViewHolder {
        val binding = ItemMyPlaylistLibraryBinding.inflate(
            LayoutInflater.from(parent.context), parent,
            false
        )
        return YourPlaylistViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return yourPlaylist.size
    }

    override fun onBindViewHolder(holder: YourPlaylistViewHolder, position: Int) {
        val playlist = yourPlaylist[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            onClick(playlist)
        }
    }

    fun setData(playlist: List<YourPlaylist>){
        val result = DiffUtil.calculateDiff(PlaylistDiffUtilCallBack(yourPlaylist, playlist))
        yourPlaylist.clear()
        yourPlaylist.addAll(playlist)
        result.dispatchUpdatesTo(this)
    }

    class PlaylistDiffUtilCallBack(private val oldList: List<YourPlaylist>, private val newList: List<YourPlaylist>): DiffUtil.Callback(){
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