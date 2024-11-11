package com.example.ncast.ui.mainApp.library.favoriteSong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ncast.R
import com.example.ncast.adapter.recycleViewAdapterLibrary.FavoriteTrackAdapter
import com.example.ncast.databinding.FragmentFavoriteSongsPlaylistBinding
import com.example.ncast.model.SpotifyService
import com.example.ncast.utils.SharePref
import com.example.ncast.utils.Track
import com.example.ncast.utils.Url
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FavoriteSongsPlaylistFragment : Fragment() {

    private var _binding: FragmentFavoriteSongsPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteSongsViewModel by viewModels {
        FavoriteSongsViewModel.FavoriteSongsViewModelFactory(requireActivity().application, getSpotifyService())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteSongsPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.visibility = View.GONE

        initRecyclerView()

        viewModel.favoriteTracks.observe(viewLifecycleOwner) { trackList ->
            (binding.recyclerViewYourFavorite.adapter as FavoriteTrackAdapter).updateData(trackList)
        }

        viewModel.loadFavoriteTracks()

        binding.back.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    private fun initRecyclerView() {
        val favoriteTrackAdapter = FavoriteTrackAdapter(emptyList()) { track ->
            viewModel.setImageUrl(track.album.images.firstOrNull()?.url)
            viewModel.setLyric(track.id)

            val action = FavoriteSongsPlaylistFragmentDirections
                .actionFavoriteSongsPlaylistFragmentToPlaySongFragment(track.id)
            findNavController().navigate(action)
        }

        binding.recyclerViewYourFavorite.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoriteTrackAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getSpotifyService(): SpotifyService {
        return Retrofit.Builder()
            .baseUrl(Url.SPOTIFY.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyService::class.java)
    }
}
