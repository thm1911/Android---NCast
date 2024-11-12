package com.example.ncast.ui.mainApp.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ncast.R
import com.example.ncast.SpacingItem
import com.example.ncast.adapter.recycleViewAdapterSearch.KeySearchAdapter
import com.example.ncast.adapter.recycleViewAdapterSearch.TrackSearchAdapter
import com.example.ncast.databinding.FragmentSearchBinding
import com.example.ncast.model.SpotifyService
import com.example.ncast.ui.mainApp.SharedViewModel
import com.example.ncast.utils.SharePref
import com.example.ncast.utils.Url
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var userId: String
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var spotifyService: SpotifyService
    private lateinit var trackSearchAdapter: TrackSearchAdapter
    private lateinit var keySearchAdapter: KeySearchAdapter
    private val viewModel: SearchViewModel by viewModels {
        SearchViewModel.SearchViewModelFactory(requireActivity().application, spotifyService)
    }
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.visibility = View.VISIBLE

        auth = FirebaseAuth.getInstance()
        spotifyService = Retrofit.Builder()
            .baseUrl(Url.SPOTIFY.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyService::class.java)

        val auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid ?: ""


        loadProfileImage()
        viewModel.loadKeyList(userId)
        initKeySearch()
        binding.textUpdateEveryHour.setText(Url.NOKEY.url)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    binding.textUpdateEveryHour.setText(Url.KEY.url)
                    viewModel.loadTrack(query)
                    initSearchTrack()
                    viewModel.saveKeySearch(query, userId)
                } else {
                    binding.textUpdateEveryHour.setText(Url.NOKEY.url)
                    viewModel.loadKeyList(userId)
                    initKeySearch()
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    binding.textUpdateEveryHour.setText(Url.KEY.url)
                    viewModel.loadTrack(query)
                    initSearchTrack()
                } else {
                    binding.textUpdateEveryHour.setText(Url.NOKEY.url)
                    viewModel.loadKeyList(userId)
                    initKeySearch()
                }
                return true
            }

        })

        val space = resources.getDimensionPixelSize(R.dimen.space_song)
        binding.recyclerView.addItemDecoration(SpacingItem(space))

    }

    private fun loadProfileImage() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("user").child(userId)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(data: DataSnapshot) {
                    val imageUrl = data.child("imageUrl").getValue(String::class.java)
                    if (imageUrl != null) {
                        if (isAdded) {
                            Glide.with(this@SearchFragment)
                                .load(imageUrl)
                                .into(binding.avt)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    private fun initSearchTrack() {
        trackSearchAdapter = TrackSearchAdapter(mutableListOf()) { track ->
            viewModel.setLyric(track.id)
            viewModel.saveKeySearch(track.name, userId)
            bottomNav.visibility = View.GONE
            SharePref.setImageUrl(requireActivity().application, track.album.images.get(0).url)
            sharedViewModel.hideMiniPlayer()
            findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToPlaySongFragment(
                    track.id
                )
            )
        }
        viewModel.trackList.observe(viewLifecycleOwner) { track ->
            trackSearchAdapter.setData(track)
        }
        binding.recyclerView.adapter = trackSearchAdapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun initKeySearch() {
        keySearchAdapter = KeySearchAdapter(
            mutableListOf(),
            { key ->
                binding.searchView.setQuery(key, true)
            },
            { key ->
                viewModel.deleteKeySearch(key, userId)
            }
        )
        viewModel.keyList.observe(viewLifecycleOwner) { key ->
            keySearchAdapter.setData(key)
        }
        binding.recyclerView.adapter = keySearchAdapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
