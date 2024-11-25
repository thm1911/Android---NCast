package com.example.ncast.model.newAlbumRelease

data class AlbumResponse(
    val album_type: String,
    val total_tracks: Int,
    val id: String,
    val images: List<NewAlbumRelease.Item.Image>,
    val name: String,
    val artists: List<NewAlbumRelease.Item.Artist>,
    val tracks: Track
) {

    data class Track(
        val items: List<Item>
    )

    data class Item(
        val artists: List<NewAlbumRelease.Item.Artist>,
        val id: String,
        val name: String
    )
}


