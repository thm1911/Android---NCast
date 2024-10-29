package com.example.ncast.ui.mainApp.home.newAlbumRelease

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ncast.database.SpotifyService
import com.example.ncast.database.newAlbumRelease.NewAlbumRelease
import com.example.ncast.database.newAlbumRelease.NewAlbumReleaseResponse
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewReleaseAlbumViewModel(private val spotifyService: SpotifyService): ViewModel() {
    private val _newAlbumReleaseList = MutableLiveData<List<NewAlbumRelease.Item>>()
    val newAlbumReleaseList: LiveData<List<NewAlbumRelease.Item>> get() = _newAlbumReleaseList

    fun loadAlbums(){
        val database = FirebaseDatabase.getInstance()
        val accessTokenRef = database.getReference("Access Token")

        accessTokenRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val accessToken = snapshot.getValue(String::class.java) ?: ""
                fetchNewAlbumRelease(accessToken)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun fetchNewAlbumRelease(accessToken: String){
        viewModelScope.launch {
            val response = spotifyService.getNewAlbumRelease("Bearer $accessToken")
            response.enqueue(object : Callback<NewAlbumReleaseResponse>{
                override fun onResponse(
                    call: Call<NewAlbumReleaseResponse>,
                    response: Response<NewAlbumReleaseResponse>
                ) {
                    if (response.isSuccessful){
                        _newAlbumReleaseList.value = response.body()?.albums?.items
                    }
                }

                override fun onFailure(call: Call<NewAlbumReleaseResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    class NewReleaseAlbumViewModelFactory(private val spotifyService: SpotifyService): ViewModelProvider
    .Factory{
            override fun <T: ViewModel> create (modelClass: Class<T>): T{
                if(modelClass.isAssignableFrom(NewReleaseAlbumViewModel::class.java)){
                    @Suppress("UNCHECKED_CAST")
                    return NewReleaseAlbumViewModel(spotifyService) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
}