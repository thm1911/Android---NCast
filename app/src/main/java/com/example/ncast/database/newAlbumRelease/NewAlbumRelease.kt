package com.example.ncast.database.newAlbumRelease


data class NewAlbumRelease(
    val items: List<Item>,
) {
    data class Item(
        val id: String,
        val images: List<Image>,
        val name: String,
        val artists: List<Artist>,
    ) {
        data class Image(
            val url: String,
        )

        data class Artist(
            val name: String,
        )
    }
}