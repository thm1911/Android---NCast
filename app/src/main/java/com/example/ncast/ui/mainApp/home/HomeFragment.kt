package com.example.ncast.ui.mainApp.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ncast.R
import com.example.ncast.SpacingItem
import com.example.ncast.adapter.recyclerViewAdapterHome.ContinueCategoryAdapter
import com.example.ncast.adapter.recyclerViewAdapterHome.NewAlbumReleaseAdapter
import com.example.ncast.adapter.recyclerViewAdapterHome.FeaturedPlaylistAdapter
import com.example.ncast.model.SpotifyService
import com.example.ncast.databinding.FragmentHomeBinding
import com.example.ncast.model.User
import com.example.ncast.ui.mainApp.home.newAlbumRelease.NewReleaseAlbumViewModel
import com.example.ncast.utils.Url
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var spotifyService: SpotifyService
    private lateinit var auth: FirebaseAuth
    private lateinit var newAlbumReleaseAdapter: NewAlbumReleaseAdapter
    private val viewModel: NewReleaseAlbumViewModel by viewModels {
        NewReleaseAlbumViewModel.NewReleaseAlbumViewModelFactory(spotifyService)
    }
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNav = requireActivity().findViewById(R.id.bottomNavigation)
        bottomNav.visibility = View.VISIBLE

        spotifyService = Retrofit.Builder()
            .baseUrl(Url.SPOTIFY.url) // Ensure Url.SPOTIFY is defined correctly
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyService::class.java)

        initNewAlbumRelease()
        viewModel.loadAlbums()
        initFeaturedPlaylist()

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId != null) {
            initUser(userId)
        }

        // Recent Music Listening
        binding.recyclerViewRecentMusicListening.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapterRecentMusic = ContinueCategoryAdapter()
        binding.recyclerViewRecentMusicListening.adapter = adapterRecentMusic

        val space = resources.getDimensionPixelSize(R.dimen.space)
        binding.recyclerViewNewAlbumRelease.addItemDecoration(SpacingItem(space))

        binding.newAlbumReleaseMore.setOnClickListener {
            bottomNav.visibility = View.GONE
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewReleaseAlbumFragment())
        }
    }

    private fun initUser(userId: String) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("user").child(userId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                val user = data.getValue(User::class.java)
                if (user != null) {
                    // Kiểm tra xem fragment có còn gắn vào Activity hay không
                    if (isAdded) {
                        binding.name.text = user.username
                        user.imageUrl?.let { imageUrl ->
                            Glide.with(this@HomeFragment)
                                .load(imageUrl)
                                .into(binding.avt)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Do nothing
            }
        })
    }

    private fun initNewAlbumRelease() {
        newAlbumReleaseAdapter = NewAlbumReleaseAdapter(mutableListOf()) { album ->
            bottomNav.visibility = View.GONE
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAlbumInforFragment(album.id))
        }
        viewModel.newAlbumReleaseList.observe(viewLifecycleOwner) { albums ->
            newAlbumReleaseAdapter.setData(albums)
            newAlbumReleaseAdapter.notifyDataSetChanged()
        }
        binding.recyclerViewNewAlbumRelease.adapter = newAlbumReleaseAdapter
        binding.recyclerViewNewAlbumRelease.layoutManager = GridLayoutManager(requireContext(), 2)
        newAlbumReleaseAdapter.showAllAlbum(false)
    }

    private fun initFeaturedPlaylist() {
        binding.recyclerViewFeaturedPlaylist.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapterTrending = FeaturedPlaylistAdapter()
        binding.recyclerViewFeaturedPlaylist.adapter = adapterTrending
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
