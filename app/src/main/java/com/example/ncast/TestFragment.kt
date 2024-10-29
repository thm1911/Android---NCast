package com.example.ncast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ncast.database.SpotifyService
import com.example.ncast.database.newAlbumRelease.NewAlbumReleaseResponse
import com.example.ncast.databinding.FragmentTestBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TestFragment : Fragment() {
    private var _binding: FragmentTestBinding? = null
    private val binding get() = _binding!!
    private lateinit var retrofit: Retrofit
    private lateinit var spotifyService: SpotifyService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTestBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        spotifyService = retrofit.create(SpotifyService::class.java)
        val database = FirebaseDatabase.getInstance()
        val accessToken = database.getReference("Access Token")

        accessToken.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val accessToken = snapshot.getValue(String::class.java) ?: ""

                binding.click.setOnClickListener {
                    val key = binding.search.text.toString()
                    getNewAlbumRelease(accessToken)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getNewAlbumRelease(accessToken: String) {
        val call = spotifyService.getNewAlbumRelease("Bearer $accessToken")
        call.enqueue(object : Callback<NewAlbumReleaseResponse> {
            override fun onResponse(call: Call<NewAlbumReleaseResponse>, response: Response<NewAlbumReleaseResponse>) {
                if (response.isSuccessful) {
                    val albums = response.body()?.albums?.items

                } else {
                }
            }

            override fun onFailure(call: Call<NewAlbumReleaseResponse>, t: Throwable) {
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}