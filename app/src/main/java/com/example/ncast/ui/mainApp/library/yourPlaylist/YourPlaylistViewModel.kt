package com.example.ncast.ui.mainApp.library.yourPlaylist

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ncast.model.SpotifyService
import com.example.ncast.model.track.TrackResponse
import com.example.ncast.model.yourPlaylist.YourPlaylist
import com.example.ncast.utils.SharePref
import com.example.ncast.utils.Track
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class YourPlaylistViewModel(private val application: Application, private val spotifyService: SpotifyService): ViewModel() {
    private val _trackList = MutableLiveData<List<TrackResponse>>()
    val trackList: LiveData<List<TrackResponse>> get() = _trackList

    fun getPlaylist(namePlaylist: String, callback: (YourPlaylist?) -> Unit){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val playlistRef = FirebaseDatabase.getInstance().getReference("user")
            .child(userId!!)
            .child("playlist")

        playlistRef.get().addOnSuccessListener { datasnapshot ->
            datasnapshot.children.forEach {
                val playlist = it.getValue(YourPlaylist::class.java)
                if(namePlaylist.equals(playlist?.name)) {
                    callback(playlist)
                    return@addOnSuccessListener
                }
            }

        }
    }

    fun loadTrack(namePlaylist: String){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val trackRef = FirebaseDatabase.getInstance().getReference("user")
            .child(userId!!)
            .child("playlist")
            .child(namePlaylist)
            .child("track")

        val accessTokenRef = FirebaseDatabase.getInstance().getReference("Access Token").child("value")
        accessTokenRef.get().addOnSuccessListener { datasnapshot ->
            val accessToken = datasnapshot.getValue(String::class.java)
            trackRef.get().addOnSuccessListener { listTrack ->
                val list = mutableListOf<TrackResponse>()
                var cnt = 0

                val trackIds = listTrack.children.mapNotNull{it.getValue(String::class.java)}
                trackIds.forEach {
                    fetchTrack(accessToken, it){track ->
                        cnt ++
                        list.add(track)

                        if(cnt == trackIds.size){
                            _trackList.value = list
                        }
                    }
                }
            }

        }
    }

    private fun fetchTrack(accessToken: String?, idTrack: String, callback: (TrackResponse) -> Unit){
        viewModelScope.launch {
            val response = spotifyService.getTrack("Bearer $accessToken", idTrack)
            response.enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    if(response.isSuccessful){
                        val track = response.body()
                        if(track != null){
                            callback(track)
                        }
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    fun setLyric(trackId: String) {
        Track.getLyricFromFirebase(trackId) { result ->
            val lyric = result
            SharePref.setLyric(application, lyric)
        }
    }

    class YourPlaylistViewModelFactory(private val application: Application, private val spotifyService: SpotifyService): ViewModelProvider
    .Factory{
            override fun <T: ViewModel> create (modelClass: Class<T>): T{
                if(modelClass.isAssignableFrom(YourPlaylistViewModel::class.java)){
                    @Suppress("UNCHECKED_CAST")
                    return YourPlaylistViewModel(application, spotifyService) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
}