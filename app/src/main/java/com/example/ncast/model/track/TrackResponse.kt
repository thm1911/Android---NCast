package com.example.ncast.model.track

data class TrackResponse(
    val album: Album,
    val artists: List<Artist>,
    val duration_ms: Int,
    val id: String,
    val name: String,
    val preview_url: String
) {
    data class Album(
        val id: String
    )

    data class Artist(
        val name: String
    )
}