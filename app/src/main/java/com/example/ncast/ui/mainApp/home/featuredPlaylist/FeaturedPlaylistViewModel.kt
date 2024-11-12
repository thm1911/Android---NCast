package com.example.ncast.ui.mainApp.home.featuredPlaylist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ncast.model.SpotifyService
import com.example.ncast.model.featuredPlaylist.FeaturedPlaylist
import com.example.ncast.model.featuredPlaylist.FeaturedPlaylistResponse
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeaturedPlaylistViewModel(private val spotifyService: SpotifyService) : ViewModel() {
    private var _featuredPlaylist = MutableLiveData<List<FeaturedPlaylist.Item>>()
    val featuredPlaylist: LiveData<List<FeaturedPlaylist.Item>> get() = _featuredPlaylist

    override fun onCleared() {
        super.onCleared()
    }

    fun loadPlaylist() {

        val database = FirebaseDatabase.getInstance()
        val accessTokenRef = database.getReference("Access Token").child("value")

        accessTokenRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val accessToken = snapshot.getValue(String::class.java) ?: ""
                fetchFeaturedPlaylist(accessToken)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun fetchFeaturedPlaylist(accessToken: String) {
        viewModelScope.launch {
            val response = spotifyService.getFeaturedPlaylist("Bearer $accessToken")
            response.enqueue(object : Callback<FeaturedPlaylistResponse> {
                override fun onResponse(
                    call: Call<FeaturedPlaylistResponse>,
                    response: Response<FeaturedPlaylistResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("Test", "Success Response")
                        val featuredPlaylist = response.body()?.playlists?.items
                        featuredPlaylist.let {
                            _featuredPlaylist.value = it
                        }
                    }
                }

                override fun onFailure(call: Call<FeaturedPlaylistResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    class FeaturedPlaylistViewModelFactory(private val spotifyService: SpotifyService) :
        ViewModelProvider
        .Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FeaturedPlaylistViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FeaturedPlaylistViewModel(spotifyService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}