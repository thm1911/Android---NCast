package com.example.ncast.model.featuredPlaylist

data class FeaturedPlaylist(
    val items: List<Item>
){
    data class Item(
        val id: String,
        val images: List<Image>,
    )

    data class Image(
        val url: String
    )

}