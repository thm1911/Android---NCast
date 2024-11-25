package com.example.ncast.model.featuredPlaylist

import com.example.ncast.model.newAlbumRelease.NewAlbumRelease

data class FeaturedPlaylist(
    val items: List<Item>
) {
    data class Item(
        val id: String,
        val images: List<NewAlbumRelease.Item.Image>,
    )

}