package com.example.ncast.adapter.recyclerViewAdapterHome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ncast.model.newAlbumRelease.NewAlbumRelease
import com.example.ncast.databinding.ItemNewAlbumReleaseHomeBinding

class NewAlbumReleaseAdapter(
    private var albums: MutableList<NewAlbumRelease.Item>,
    private val onClick: (NewAlbumRelease.Item) -> Unit
) :
    RecyclerView.Adapter<NewAlbumReleaseAdapter.NewAlbumReleaseViewHolder>() {
    private var showAll: Boolean = false

    inner class NewAlbumReleaseViewHolder(private val binding: ItemNewAlbumReleaseHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val image = binding.itemImage
        val title = binding.itemTitle
        val artist = binding.itemArtist

        fun bind(album: NewAlbumRelease.Item) {
            Glide.with(image.context)
                .load(album.images.get(0).url)
                .into(image)

            title.setText(album.name)
            artist.setText(album.artists.get(0).name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewAlbumReleaseViewHolder {
        val binding = ItemNewAlbumReleaseHomeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewAlbumReleaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewAlbumReleaseViewHolder, position: Int) {
        val album = albums[position]
        holder.bind(album)
        holder.itemView.setOnClickListener {
            onClick(album)
        }
    }

    override fun getItemCount(): Int {
        if (showAll) return albums.size
        else return minOf(albums.size, 4)
    }

    fun showAllAlbum(check: Boolean) {
        showAll = check
        notifyDataSetChanged()
    }

    fun setData(albums: List<NewAlbumRelease.Item>) {
        val result = DiffUtil.calculateDiff(AlbumDiffUtilCallBack(this.albums, albums))
        this.albums.clear()
        this.albums.addAll(albums)
        result.dispatchUpdatesTo(this)
    }

    class AlbumDiffUtilCallBack(
        private val oldList: List<NewAlbumRelease.Item>,
        private val newList: List<NewAlbumRelease.Item>
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