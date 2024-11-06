package com.example.ncast.model

import com.example.ncast.model.featuredPlaylist.FeaturedPlaylistResponse
import com.example.ncast.model.featuredPlaylist.PlaylistResponse
import com.example.ncast.model.newAlbumRelease.AlbumResponse
import com.example.ncast.model.newAlbumRelease.NewAlbumReleaseResponse
import com.example.ncast.model.track.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyService {
    @GET("v1/browse/new-releases")
    fun getNewAlbumRelease(
        @Header("Authorization") accessToken: String,
        @Query("limit") limit: Int = 10,
    ): Call<NewAlbumReleaseResponse>

    @GET("v1/albums/{id}")
    fun getAlbum(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String
    ): Call<AlbumResponse>

    @GET("v1/tracks/{id}")
    fun getTrack(
        @Header("Authorization") accessToken: String,
        @Path("id") id: String
    ): Call<TrackResponse>

    @GET("v1/browse/featured-playlists")
    fun getFeaturedPlaylist(
        @Header("Authorization") accessToken: String,
        @Query("limit") limit: Int = 10,
    ): Call<FeaturedPlaylistResponse>

    @GET("v1/playlists/{playlist_id}")
    fun getPlaylist(
        @Header("Authorization") accessToken: String,
        @Path("playlist_id") id: String
    ): Call<PlaylistResponse>
}