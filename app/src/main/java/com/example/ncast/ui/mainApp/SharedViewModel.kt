package com.example.ncast.ui.mainApp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {
    private var _trackId = MutableLiveData<String>()
    private var _nameTrack = MutableLiveData<String>()
    private var _artistTrack = MutableLiveData<String>()
    val trackId get() = _trackId
    val nameTrack get() = _nameTrack
    val artistTrack get() = _artistTrack
    private val _isMiniPlayerVisible = MutableLiveData(false)
    val isMiniPlayerVisible: LiveData<Boolean> get() = _isMiniPlayerVisible

    fun setNameTrack(nameTrack: String){
        _nameTrack.value = nameTrack
    }

    fun setArtistTrack(artistTrack: String){
        _artistTrack.value = artistTrack
    }

    fun setIdTrack(trackId: String){
        _trackId.value = trackId
    }

    fun showMiniPlayer() {
        _isMiniPlayerVisible.value = true
    }

    fun hideMiniPlayer() {
        _isMiniPlayerVisible.value = false
    }
}