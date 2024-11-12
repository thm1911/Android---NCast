package com.example.ncast.ui.mainApp.home.recentPlaySongs

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ncast.model.SpotifyService
import com.example.ncast.model.track.TrackResponse
import com.example.ncast.utils.SharePref
import com.example.ncast.utils.Track
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(
    private val application: Application,
    private val spotifyService: SpotifyService
) : ViewModel() {

    private val _recentMusicList = MutableLiveData<List<TrackResponse>>()
    val recentMusicList: LiveData<List<TrackResponse>> = _recentMusicList

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> = _imageUrl

    private val _lyric = MutableLiveData<String>()
    val lyric: LiveData<String> = _lyric

    fun loadRecentMusicListening(userId: String) {
        val database = FirebaseDatabase.getInstance()
        val recentTracksRef = database.getReference("user/$userId/recently_played")

        recentTracksRef.limitToLast(8)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val trackIds = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                    fetchTracksData(trackIds)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private fun fetchTracksData(trackIds: List<String>) {
        val tracks = mutableListOf<TrackResponse>()
        trackIds.forEachIndexed { _, trackId ->
            fetchTrackData(trackId) { track ->
                tracks.add(track)
                if (tracks.size == trackIds.size) {
                    val sortedTracks = trackIds.map { id -> tracks.first { it.id == id } }
                    _recentMusicList.postValue(sortedTracks)
                }
            }
        }
    }

    private fun fetchTrackData(trackId: String, onTrackFetched: (TrackResponse) -> Unit) {
        getAccessTokenFromFirebase { accessToken ->
            val response = spotifyService.getTrack("Bearer $accessToken", trackId)
            response.enqueue(object : Callback<TrackResponse> {
                override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let(onTrackFetched)
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                }
            })
        }
    }

    private fun getAccessTokenFromFirebase(onTokenReceived: (String) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val accessTokenRef = database.getReference("Access Token").child("value")

        accessTokenRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val accessToken = snapshot.getValue(String::class.java) ?: ""
                onTokenReceived(accessToken)
            }

            override fun onCancelled(error: DatabaseError) {
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
    class HomeViewModelFactory(
        private val application: Application,
        private val spotifyService: SpotifyService
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(application, spotifyService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}
