package com.example.ncast.database.newAlbumRelease

data class AlbumResponse(
    val album_type: String,
    val total_tracks: Int,
    val id: String,
    val images: List<Image>,
    val name: String,
    val artists: List<Artist>,
    val tracks: Track
){
    data class Image(
        val url: String
    )

    data class Artist(
        val name: String
    )

    data class Track(
        val items: List<Item>
    )

    data class Item(
        val artists: List<Artist>,
        val id: String,
        val name: String,
        val preview_url: String,
    )
}


