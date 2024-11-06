package com.example.ncast.ui.mainApp.home.featuredPlaylist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ncast.model.SpotifyService
import com.example.ncast.model.featuredPlaylist.PlaylistResponse
import com.example.ncast.utils.AccessToken
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaylistViewModel(private val spotifyService: SpotifyService): ViewModel() {
    private var _playlist = MutableLiveData<PlaylistResponse>()
    val playlist: LiveData<PlaylistResponse> get() = _playlist
    private var _trackList = MutableLiveData<List<PlaylistResponse.Item>>()
    val trackList: LiveData<List<PlaylistResponse.Item>> get() = _trackList

    fun loadPlaylist(id: String){
        val database = FirebaseDatabase.getInstance()
        val accessTokenRef = database.getReference("Access Token")
        accessTokenRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val accessToken = snapshot.getValue(String::class.java) ?: ""
                fetchPlaylist(accessToken, id)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun fetchPlaylist(accessToken: String, id: String){
        val response = spotifyService.getPlaylist("Bearer $accessToken", id)
        response.enqueue(object : Callback<PlaylistResponse>{
            override fun onResponse(
                call: Call<PlaylistResponse>,
                response: Response<PlaylistResponse>
            ) {
                if (response.isSuccessful){
                    val playlist = response.body()
                    playlist.let {
                        _playlist.value = it
                        _trackList.value = it?.tracks?.items

                        val database = FirebaseDatabase.getInstance()
                        val playlistInfor = database.getReference("Playlist Information")

                        playlistInfor.setValue(it)
                    }
                }

                else{
                    Log.d("Test", "Failed")
                }
            }

            override fun onFailure(call: Call<PlaylistResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    class PlaylistViewModelFactory(private val spotifyService: SpotifyService): ViewModelProvider
    .Factory{
            override fun <T: ViewModel> create (modelClass: Class<T>): T{
                if(modelClass.isAssignableFrom(PlaylistViewModel::class.java)){
                    @Suppress("UNCHECKED_CAST")
                    return PlaylistViewModel(spotifyService) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
}