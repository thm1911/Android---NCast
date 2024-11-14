package com.example.ncast.ui.mainApp.library

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
                    Log.d("test", it.name + " " + it.imageUrl)
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