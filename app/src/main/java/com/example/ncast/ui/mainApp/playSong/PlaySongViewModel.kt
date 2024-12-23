package com.example.ncast.ui.mainApp.playSong

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ncast.model.SpotifyService
import com.example.ncast.model.track.TrackResponse
import com.example.ncast.utils.SharePref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaySongViewModel(
    private val application: Application,
    private val spotifyService: SpotifyService
) : ViewModel() {
    private var _track = MutableLiveData<TrackResponse>()
    val track: LiveData<TrackResponse> get() = _track

    fun loadTrack(idTrack: String) {
        val database = FirebaseDatabase.getInstance()
        val accessTokenRef = database.getReference("Access Token").child("value")
        accessTokenRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val accessToken = snapshot.getValue(String::class.java) ?: ""
                fetchTrack(accessToken, idTrack)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun fetchTrack(accessToken: String, id: String) {
        val response = spotifyService.getTrack("Bearer $accessToken", id)
        response.enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful) {
                    val track = response.body()
                    if (track != null) {
                        track.let {
                            _track.value = it
//                            fetchFirebaseData(it)
                            saveTrackToRecentlyPlayed(it.id)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
            }

        })
    }

    private fun saveTrackToRecentlyPlayed(trackId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val recentTracksRef = database.getReference("user/$userId/recently_played")

        recentTracksRef.get().addOnSuccessListener { snapshot ->
            val recentList = mutableListOf<String>()
            snapshot.children.mapNotNullTo(recentList) { it.getValue(String::class.java) }

            if (recentList.contains(trackId)) {
                recentList.remove(trackId)
            }
            recentList.add(0, trackId)

            if (recentList.size > 8) {
                recentList.removeLast()
            }

            recentTracksRef.setValue(recentList)
        }
    }

    class PlaySongViewModelFactory(
        private val application: Application,
        private val spotifyService: SpotifyService
    ) : ViewModelProvider
    .Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlaySongViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PlaySongViewModel(application, spotifyService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}