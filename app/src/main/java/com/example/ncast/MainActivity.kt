package com.example.ncast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ncast.databinding.ActivityMainBinding
import com.example.ncast.utils.AccessToken
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var exoPlayer: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        exoPlayer = ExoPlayer.Builder(this).build()
        AccessToken.getAccessToken { token ->
            val database = FirebaseDatabase.getInstance()
            val accessTokenRef = database.getReference("Access Token").child("value")
            accessTokenRef.setValue(token)
        }

    }

    fun getExoPlayer(): ExoPlayer{
        return exoPlayer
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
        _binding = null
    }
}