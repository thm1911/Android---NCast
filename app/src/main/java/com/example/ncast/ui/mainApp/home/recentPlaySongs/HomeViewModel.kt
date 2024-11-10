package com.example.ncast.ui.mainApp.home.recentPlaySongs

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ncast.model.SpotifyService
import com.example.ncast.utils.SharePref
import com.example.ncast.utils.Track

class HomeViewModel(
    private val application: Application,
    private val spotifyService: SpotifyService
) : ViewModel() {

    fun setLyric(trackId: String) {
        Track.getLyricFromFirebase(trackId) { lyric ->
            SharePref.setLyric(application, lyric)
        }
    }

    fun setImageUrl(imageUrl: String) {
        SharePref.setImageUrl(application, imageUrl)
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
