package com.example.ncast.model.featuredPlaylist

import com.example.ncast.model.newAlbumRelease.AlbumResponse
import com.example.ncast.model.newAlbumRelease.NewAlbumRelease
import com.example.ncast.model.track.TrackResponse

data class PlaylistResponse(
    val followers: Follower,
    val id: String,
    val images: List<NewAlbumRelease.Item.Image>,
    val name: String,
    val tracks: Track
){
    data class Follower(
        val total: Int
    )

    data class Track(
        val total: Int,
        val items: List<Item>
    )

    data class Item(
        val track: TrackObject
    )

    data class TrackObject(
        val id: String,
        val artists: List<NewAlbumRelease.Item.Artist>,
        val album: Album,
        val name: String
    )

    data class Album(
        val images: List<NewAlbumRelease.Item.Image>
    )
}