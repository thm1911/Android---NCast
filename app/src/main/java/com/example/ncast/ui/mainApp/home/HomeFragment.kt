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
import com.example.ncast.adapter.recyclerViewAdapterHome.NewAlbumReleaseAdapter
import com.example.ncast.adapter.recyclerViewAdapterHome.FeaturedPlaylistAdapter
import com.example.ncast.adapter.recyclerViewAdapterHome.RecentMusicAdapter
import com.example.ncast.model.SpotifyService
import com.example.ncast.databinding.FragmentHomeBinding
import com.example.ncast.model.User
import com.example.ncast.model.track.TrackResponse
import com.example.ncast.ui.mainApp.home.featuredPlaylist.FeaturedPlaylistViewModel
import com.example.ncast.ui.mainApp.home.newAlbumRelease.NewReleaseAlbumViewModel
import com.example.ncast.ui.mainApp.home.recentPlaySongs.HomeViewModel
import com.example.ncast.utils.SharePref
import com.example.ncast.utils.Url
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var spotifyService: SpotifyService
    private lateinit var auth: FirebaseAuth
    private lateinit var newAlbumReleaseAdapter: NewAlbumReleaseAdapter
    private lateinit var featuredPlaylistAdapter: FeaturedPlaylistAdapter
    private val newReleaseAlbumViewModel: NewReleaseAlbumViewModel by viewModels {
        NewReleaseAlbumViewModel.NewReleaseAlbumViewModelFactory(spotifyService)
    }
    private val featuredPlaylistViewModel: FeaturedPlaylistViewModel by viewModels {
        FeaturedPlaylistViewModel.FeaturedPlaylistViewModelFactory(spotifyService)
    }
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var recentMusicAdapter: RecentMusicAdapter

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModel.HomeViewModelFactory(requireActivity().application, spotifyService)
    }



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
            .baseUrl(Url.SPOTIFY.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyService::class.java)

        initNewAlbumRelease()
        newReleaseAlbumViewModel.loadAlbums()

        initFeaturedPlaylist()
        featuredPlaylistViewModel.loadPlaylist()

        initRecyclerView()

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId != null) {
            initUser(userId)
            viewModel.loadRecentMusicListening(userId)
        }

        initRecyclerView()
        viewModel.recentMusicList.observe(viewLifecycleOwner) { trackList ->
            recentMusicAdapter.updateData(trackList)
        }

        val spaces = resources.getDimensionPixelSize(R.dimen.space)
        binding.recyclerViewRecentMusicListening.addItemDecoration(SpacingItem(spaces))
        binding.recyclerViewRecentMusicListening.adapter = recentMusicAdapter
        binding.recyclerViewRecentMusicListening.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        binding.recyclerViewRecentMusicListening.adapter = recentMusicAdapter
        binding.recyclerViewRecentMusicListening.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val space = resources.getDimensionPixelSize(R.dimen.space)
        binding.recyclerViewNewAlbumRelease.addItemDecoration(SpacingItem(space))

        binding.newAlbumReleaseMore.setOnClickListener {
            bottomNav.visibility = View.GONE
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewReleaseAlbumFragment())
        }

        binding.featuredPlaylistMore.setOnClickListener {
            bottomNav.visibility = View.GONE
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFeaturedPlaylistFragment())
        }
    }


    private fun initRecyclerView() {
        recentMusicAdapter = RecentMusicAdapter(emptyList()) { track ->
            viewModel.setImageUrl(track.album.images.firstOrNull()?.url)
            viewModel.setLyric(track.id)

            val action = HomeFragmentDirections
                .actionHomeFragmentToPlaySongFragment(track.id)
            findNavController().navigate(action)
            bottomNav.visibility = View.GONE
        }

        binding.recyclerViewRecentMusicListening.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentMusicAdapter
        }
    }

    private fun initUser(userId: String) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("user").child(userId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                val user = data.getValue(User::class.java)
                if (user != null) {
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
            }
        })
    }

    private fun initNewAlbumRelease() {
        newAlbumReleaseAdapter = NewAlbumReleaseAdapter(mutableListOf()) { album ->
            bottomNav.visibility = View.GONE
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToAlbumInforFragment(
                    album.id
                )
            )
        }
        newReleaseAlbumViewModel.newAlbumReleaseList.observe(viewLifecycleOwner) { albums ->
            newAlbumReleaseAdapter.setData(albums)
            newAlbumReleaseAdapter.notifyDataSetChanged()
        }
        binding.recyclerViewNewAlbumRelease.adapter = newAlbumReleaseAdapter
        binding.recyclerViewNewAlbumRelease.layoutManager = GridLayoutManager(requireContext(), 2)
        newAlbumReleaseAdapter.showAllAlbum(false)
    }

    private fun initFeaturedPlaylist() {
        featuredPlaylistAdapter = FeaturedPlaylistAdapter(mutableListOf()) { playlist ->
            bottomNav.visibility = View.GONE
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToPlaylistFragment(
                    playlist.id
                )
            )
        }
        featuredPlaylistViewModel.featuredPlaylist.observe(viewLifecycleOwner) { playlist ->
            featuredPlaylistAdapter.setData(playlist)
            featuredPlaylistAdapter.notifyDataSetChanged()
        }
        binding.recyclerViewFeaturedPlaylist.adapter = featuredPlaylistAdapter
        binding.recyclerViewFeaturedPlaylist.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        featuredPlaylistAdapter.showAllPlaylist(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
