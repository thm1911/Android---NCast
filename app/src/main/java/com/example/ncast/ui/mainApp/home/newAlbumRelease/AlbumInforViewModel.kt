package com.example.ncast.ui.mainApp.home.newAlbumRelease

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ncast.database.SpotifyService
import com.example.ncast.database.newAlbumRelease.AlbumResponse
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlbumInforViewModel(private val spotifyService: SpotifyService): ViewModel() {
    private var _album = MutableLiveData<AlbumResponse>()
    val album: LiveData<AlbumResponse> get() = _album

    private val _songList = MutableLiveData<List<AlbumResponse.Item>>()
    val songList: LiveData<List<AlbumResponse.Item>> get() = _songList

    fun loadAlbums(id: String){
        val database = FirebaseDatabase.getInstance()
        val accessTokenRef = database.getReference("Access Token")

        accessTokenRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val accessToken = snapshot.getValue(String::class.java) ?: ""
                fetchAlbum(accessToken, id)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun fetchAlbum(accessToken: String, id: String){
        viewModelScope.launch {
            val response = spotifyService.getAlbum("Bearer $accessToken", id)
            response.enqueue(object : Callback<AlbumResponse>{
                override fun onResponse(
                    call: Call<AlbumResponse>,
                    response: Response<AlbumResponse>
                ) {
                    if (response.isSuccessful) {
                        val albums = response.body()
                        albums.let {
                            _album.value = albums
                            _songList.value= albums?.tracks?.items
                        }
                    }
                }

                override fun onFailure(call: Call<AlbumResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    class AlbumInforViewModelFactory(private val spotifyService: SpotifyService): ViewModelProvider
    .Factory{
            override fun <T: ViewModel> create (modelClass: Class<T>): T{
                if(modelClass.isAssignableFrom(AlbumInforViewModel::class.java)){
                    @Suppress("UNCHECKED_CAST")
                    return AlbumInforViewModel(spotifyService) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
}