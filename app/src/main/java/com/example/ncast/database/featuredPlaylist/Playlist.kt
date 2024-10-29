package com.example.ncast.database.featuredPlaylist

data class Playlist(
    val items: List<Item>
){
    data class Item(
        val id: String,
        val images: List<Image>,
        val name: String,
    ){
        data class Image(
            val url: String
        )


    }

}