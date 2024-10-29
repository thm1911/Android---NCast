package com.example.ncast

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ncast.databinding.ActivityMainBinding
import com.example.ncast.utils.AccessToken
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AccessToken.getAccessToken{token ->
            val database = FirebaseDatabase.getInstance()
            val accessTokenRef = database.getReference("Access Token")
            accessTokenRef.setValue(token)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}