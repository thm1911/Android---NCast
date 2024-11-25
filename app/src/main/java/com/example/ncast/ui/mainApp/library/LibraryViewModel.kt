package com.example.ncast.ui.mainApp.library

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.util.recursiveFetchLongSparseArray
import com.example.ncast.model.yourPlaylist.YourPlaylist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LibraryViewModel: ViewModel() {
    private val _playlist = MutableLiveData<List<YourPlaylist>>()
    val playlist: LiveData<List<YourPlaylist>> get() = _playlist

    fun loadPlaylist(){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val playlistRef = FirebaseDatabase.getInstance().getReference("user").child(userId!!).child("playlist")
        val result = mutableListOf<YourPlaylist>()
        playlistRef.get().addOnSuccessListener { dataSnapshot ->
            dataSnapshot.children.forEach { snapshot ->
                snapshot.getValue(YourPlaylist::class.java)?.let {
                    result.add(it)
                }
            }
            _playlist.value = result
        }
    }

    fun getPlaylistContainsTrack(idTrack: String){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val playlistRef = FirebaseDatabase.getInstance().getReference("user").child(userId!!).child("playlist")

        playlistRef.get().addOnSuccessListener { dataSnapshot ->
            val result = mutableListOf<YourPlaylist>()
            dataSnapshot.children.forEach { snapshot ->
                val playlist = snapshot.getValue(YourPlaylist::class.java)
                playlist?.let {
                    val trackRef = snapshot.child("track")
                    trackRef.children.forEach{id ->
                        if(id.value?.equals(idTrack)!!){
                            playlist.ticked = true
                        }
                    }
                }
                playlist?.let {
                    result.add(it)
                }
            }
            _playlist.value = result
        }
    }

    class LibraryViewModelFactory(): ViewModelProvider
    .Factory{
            override fun <T: ViewModel> create (modelClass: Class<T>): T{
                if(modelClass.isAssignableFrom(LibraryViewModel::class.java)){
                    @Suppress("UNCHECKED_CAST")
                    return LibraryViewModel() as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
}