package com.example.ncast.ui.mainApp.library.favoriteSong

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ncast.model.SpotifyService
import com.example.ncast.model.track.TrackResponse
import com.example.ncast.utils.SharePref
import com.example.ncast.utils.Track
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteSongsViewModel(
    private val application: Application,
    private val spotifyService: SpotifyService
) : ViewModel() {

    private val _favoriteTracks = MutableLiveData<List<TrackResponse>>()
    val favoriteTracks: LiveData<List<TrackResponse>> get() = _favoriteTracks

    fun loadFavoriteTracks() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val favouriteTracksRef = database.getReference("user/$userId/favourite_tracks")

        favouriteTracksRef.get().addOnSuccessListener { snapshot ->
            val trackIds = snapshot.children.mapIndexed { index, child ->
                index to child.getValue(String::class.java)
            }.filter { it.second != null }.map { it.first to it.second!! }

            val trackList = mutableListOf<Pair<Int, TrackResponse>>()
            val accessTokenRef = database.getReference("Access Token").child("value")

            accessTokenRef.get().addOnSuccessListener { tokenSnapshot ->
                val accessToken = tokenSnapshot.getValue(String::class.java) ?: ""
                trackIds.forEach { (order, trackId) ->
                    fetchTrackData(trackId, accessToken) { track ->
                        track?.let {
                            trackList.add(order to it)
                            if (trackList.size == trackIds.size) {
                                _favoriteTracks.value = trackList.sortedBy { it.first }.map { it.second }
                            }
                        }
                    }
                }
            }
        }
    }


    private fun fetchTrackData(trackId: String, accessToken: String, onTrackFetched: (TrackResponse?) -> Unit) {
        spotifyService.getTrack("Bearer $accessToken", trackId).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful) {
                    onTrackFetched(response.body())
                } else {
                    onTrackFetched(null)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                onTrackFetched(null)
            }
        })
    }

    fun setImageUrl(imageUrl: String?) {
        imageUrl?.let {
            SharePref.setImageUrl(application, it)
        }
    }

    fun setLyric(trackId: String) {
        Track.getLyricFromFirebase(trackId) { lyric ->
            SharePref.setLyric(application, lyric)
        }
    }

    class FavoriteSongsViewModelFactory(
        private val application: Application,
        private val spotifyService: SpotifyService
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoriteSongsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FavoriteSongsViewModel(application, spotifyService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
