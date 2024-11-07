package com.example.ncast.ui.mainApp.search

import Tracks
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ncast.model.SpotifyService
import com.example.ncast.model.search.SearchResponse
import com.example.ncast.utils.SharePref
import com.example.ncast.utils.Track
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Appendable

class SearchViewModel(
    private val application: Application,
    private val spotifyService: SpotifyService
) : ViewModel() {
    private var _keyList = MutableLiveData<List<String>>()
    val keyList: LiveData<List<String>> get() = _keyList
    private var _trackList = MutableLiveData<List<Tracks.Item>>()
    val trackList: LiveData<List<Tracks.Item>> get() = _trackList

    fun loadKeyList(userId: String) {
        val database = FirebaseDatabase.getInstance()
        val keyRef = database.getReference("user").child(userId).child("Key Search")
        val keys = mutableListOf<String>()
        keyRef.get().addOnSuccessListener { dataSnapshot ->
            dataSnapshot.children.forEach { snapShot ->
                snapShot.getValue(String::class.java)?.let {
                    keys.add(it)
                }
            }
            _keyList.value = keys
        }
    }

    fun saveKeySearch(key: String, userId: String) {
        val database = FirebaseDatabase.getInstance()
        val keyRef = database.getReference("user").child(userId).child("Key Search")

        val updateKeyList = _keyList.value?.toMutableList() ?: mutableListOf()
        if (updateKeyList.contains(key)) {
            updateKeyList.remove(key)
        }
        updateKeyList.add(0, key)

        if (updateKeyList.size > 10) {
            updateKeyList.removeLast()
        }
        _keyList.value = updateKeyList
        keyRef.setValue(updateKeyList)
    }

    fun deleteKeySearch(key: String, userId: String) {
        val keys = _keyList.value?.toMutableList() ?: mutableListOf()
        keys.remove(key)
        _keyList.value = keys

        val database = FirebaseDatabase.getInstance()
        val keyRef = database.getReference("user").child(userId).child("Key Search")

        keyRef.setValue(keys)
    }

    fun loadTrack(key: String) {
        val database = FirebaseDatabase.getInstance()
        val accessTokenRef = database.getReference("Access Token").child("value")

        accessTokenRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val accessToken = snapshot.getValue(String::class.java) ?: ""
                fetchTrackSearch(accessToken, key)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun fetchTrackSearch(accessToken: String, key: String) {
        val respone = spotifyService.search("Bearer $accessToken", key)
        respone.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                if (response.isSuccessful) {
                    val track = response.body()?.tracks?.items
                    track.let {
                        _trackList.value = it
                    }
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    fun setLyric(trackId: String) {
        Track.getLyricFromFirebase(trackId) { result ->
            val lyric = result
            SharePref.setLyric(application, lyric)
        }
    }

    class SearchViewModelFactory(
        private val application: Application,
        private val spotifyService: SpotifyService
    ) : ViewModelProvider
    .Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SearchViewModel(application, spotifyService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}