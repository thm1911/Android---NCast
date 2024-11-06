package com.example.ncast.model.track

data class TrackResponse(
    val album: Album,
    val artists: List<Artist>,
    val duration_ms: Int,
    val id: String,
    val name: String,
    var preview_url: String = "",
//    var lyric: String = ""
) {
    data class Album(
        val id: String
    )

    data class Artist(
        val name: String
    )
}