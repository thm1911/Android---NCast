package com.example.ncast.database

import com.example.ncast.database.newAlbumRelease.AlbumResponse
import com.example.ncast.database.newAlbumRelease.NewAlbumReleaseResponse
import com.example.ncast.database.song.SearchResponse
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
}