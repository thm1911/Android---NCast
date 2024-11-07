package com.example.ncast

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.example.ncast.databinding.ActivityMainBinding
import com.example.ncast.ui.mainApp.MainAppFragment
import com.example.ncast.ui.mainApp.home.HomeFragment
import com.example.ncast.ui.onBoarding.OnboardingFragment
import com.example.ncast.utils.AccessToken
import com.example.ncast.utils.SharePref
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
            val accessTokenRef = database.getReference("Access Token").child("value")
            accessTokenRef.setValue(token)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}