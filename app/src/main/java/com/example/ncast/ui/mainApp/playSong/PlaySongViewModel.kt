package com.example.ncast.ui.mainApp.playSong

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ncast.model.SpotifyService
import com.example.ncast.model.track.TrackResponse
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaySongViewModel(private val spotifyService: SpotifyService): ViewModel() {
    private var _track = MutableLiveData<TrackResponse>()
    val track: LiveData<TrackResponse> get() = _track

    fun loadTrack(idTrack: String){
        val database = FirebaseDatabase.getInstance()
        val accessTokenRef = database.getReference("Access Token")
        accessTokenRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val accessToken = snapshot.getValue(String::class.java) ?: ""
                fetchTrack(accessToken, idTrack)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun fetchTrack(accessToken: String, id: String){
        val response = spotifyService.getTrack("Bearer $accessToken", id)
        response.enqueue(object : Callback<TrackResponse>{
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful){
                    val track = response.body()
                    if (track != null) {
                        track.let {
                            _track.value = it
                        }
                    }
                    else{
                    }
                }
                else{
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
            }

        })
    }

    class PlaySongViewModelFactory(private val spotifyService: SpotifyService): ViewModelProvider
    .Factory{
            override fun <T: ViewModel> create (modelClass: Class<T>): T{
                if(modelClass.isAssignableFrom(PlaySongViewModel::class.java)){
                    @Suppress("UNCHECKED_CAST")
                    return PlaySongViewModel(spotifyService) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
}