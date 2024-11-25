package com.example.ncast.ui.mainApp.library.yourPlaylist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ncast.R
import com.example.ncast.SpacingItem
import com.example.ncast.adapter.recyclerViewAdapterHome.PlaylistTrackAdapter
import com.example.ncast.adapter.recyclerViewAdapterHome.RecentMusicAdapter
import com.example.ncast.databinding.FragmentYourPlaylistBinding
import com.example.ncast.model.SpotifyService
import com.example.ncast.model.yourPlaylist.YourPlaylist
import com.example.ncast.ui.mainApp.SharedViewModel
import com.example.ncast.ui.mainApp.home.featuredPlaylist.PlaylistFragment
import com.example.ncast.ui.mainApp.home.featuredPlaylist.PlaylistFragmentDirections
import com.example.ncast.utils.SharePref
import com.example.ncast.utils.Url
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class YourPlaylistFragment : Fragment() {
    private var _binding: FragmentYourPlaylistBinding? = null
    private val binding get() = _binding!!
    private val args: YourPlaylistFragmentArgs by navArgs()
    private lateinit var namePlaylist: String
    private lateinit var spotifyService: SpotifyService
    private lateinit var adapter: RecentMusicAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: YourPlaylistViewModel by viewModels {
        YourPlaylistViewModel.YourPlaylistViewModelFactory(requireActivity().application, spotifyService)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentYourPlaylistBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        namePlaylist = args.name
        spotifyService = Retrofit.Builder()
            .baseUrl(Url.SPOTIFY.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyService::class.java)

        viewModel.getPlaylist(namePlaylist){yourPlaylist ->
            binding.name.setText(yourPlaylist?.name)
            Glide.with(binding.image.context)
                .load(yourPlaylist?.imageUrl)
                .into(binding.image)
        }

        initTrack()

        binding.close.setOnClickListener{
            findNavController().popBackStack()
        }

        val space = resources.getDimensionPixelSize(R.dimen.space)
        binding.recyclerView.addItemDecoration(SpacingItem(space))
    }

    private fun initTrack(){
        adapter = RecentMusicAdapter(mutableListOf()){ track ->
            viewModel.setLyric(track.id)
            SharePref.setImageUrl(
                requireActivity().application,
                track.album.images.get(0).url
            )
            sharedViewModel.hideMiniPlayer()
            findNavController().navigate(
                YourPlaylistFragmentDirections.actionYourPlaylistFragmentToPlaySongFragment(track.id)
            )
        }

        viewModel.loadTrack(namePlaylist)
        viewModel.trackList.observe(viewLifecycleOwner){trackList ->
            binding.more.setText(String.format("%d tracks", trackList.size))
            adapter.updateData(trackList)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}