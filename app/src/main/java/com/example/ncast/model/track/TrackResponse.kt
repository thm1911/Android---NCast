package com.example.ncast.model.track

import com.example.ncast.model.newAlbumRelease.NewAlbumRelease

data class TrackResponse(
    val album: Album,
    val artists: List<NewAlbumRelease.Item.Artist>,
    val id: String,
    val name: String,
    var preview_url: String = "",
//    var lyric: String = ""
) {
    data class Album(
        val id: String
    )
}