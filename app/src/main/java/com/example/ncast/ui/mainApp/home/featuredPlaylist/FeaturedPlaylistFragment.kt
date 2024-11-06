package com.example.ncast.ui.mainApp.home.featuredPlaylist

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ncast.R
import com.example.ncast.SpacingItem
import com.example.ncast.adapter.recyclerViewAdapterHome.FeaturedPlaylistAdapter
import com.example.ncast.databinding.FragmentFeaturedPlaylistBinding
import com.example.ncast.model.SpotifyService
import com.example.ncast.utils.Url
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FeaturedPlaylistFragment : Fragment() {
    private var _binding: FragmentFeaturedPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var spotifyService: SpotifyService
    private lateinit var featuredPlaylistAdapter: FeaturedPlaylistAdapter
    private val viewModel: FeaturedPlaylistViewModel by viewModels {
        FeaturedPlaylistViewModel.FeaturedPlaylistViewModelFactory(spotifyService)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeaturedPlaylistBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spotifyService = Retrofit.Builder()
            .baseUrl(Url.SPOTIFY.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyService::class.java)


        initRecyclerView()
        viewModel.loadPlaylist()
        val spacingItem = resources.getDimensionPixelSize(R.dimen.space_featured_playlist)
        binding.recyclerViewFeaturedPlaylist.addItemDecoration(SpacingItem(spacingItem))

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initRecyclerView(){
        featuredPlaylistAdapter = FeaturedPlaylistAdapter(mutableListOf()){playlist ->
            findNavController().navigate(FeaturedPlaylistFragmentDirections.actionFeaturedPlaylistFragmentToPlaylistFragment(playlist.id))
        }
        viewModel.featuredPlaylist.observe(viewLifecycleOwner){playlist ->
            featuredPlaylistAdapter.setData(playlist)
            featuredPlaylistAdapter.notifyDataSetChanged()
        }
        binding.recyclerViewFeaturedPlaylist.adapter = featuredPlaylistAdapter
        binding.recyclerViewFeaturedPlaylist.layoutManager = GridLayoutManager(requireContext(), 2)
        featuredPlaylistAdapter.showAllPlaylist(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}