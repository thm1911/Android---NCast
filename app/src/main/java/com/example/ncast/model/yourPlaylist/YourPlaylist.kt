package com.example.ncast.model.yourPlaylist

import com.example.ncast.model.track.TrackResponse

data class YourPlaylist(
    var imageUrl: String = "",
    var name: String = "",
    var ticked: Boolean = false,
    val idTrack: MutableList<String> = mutableListOf()
)