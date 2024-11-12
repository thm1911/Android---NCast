package com.example.ncast.ui.mainApp.home.featuredPlaylist

import android.os.Bundle
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
import com.example.ncast.adapter.recyclerViewAdapterHome.AlbumTrackAdapter
import com.example.ncast.adapter.recyclerViewAdapterHome.PlaylistTrackAdapter
import com.example.ncast.databinding.FragmentPlaylistBinding
import com.example.ncast.model.SpotifyService
import com.example.ncast.ui.mainApp.SharedViewModel
import com.example.ncast.ui.mainApp.playSong.PlaySongFragment
import com.example.ncast.utils.SharePref
import com.example.ncast.utils.Url
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var spotifyService: SpotifyService
    private lateinit var trackAdapter: PlaylistTrackAdapter
    private val args: PlaylistFragmentArgs by navArgs()
    private val viewModel: PlaylistViewModel by viewModels {
        PlaylistViewModel.PlaylistViewModelFactory(requireActivity().application, spotifyService)
    }
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = args.playlistId
        spotifyService = Retrofit.Builder()
            .baseUrl(Url.SPOTIFY.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyService::class.java)

        viewModel.loadPlaylist(playlistId)

        initPlaylist()
        initTrack()

        binding.close.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initPlaylist() {
        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            Glide.with(this)
                .load(playlist.images.get(0).url)
                .into(binding.image)

            binding.albumType.setText("")
            binding.name.setText(playlist.name)
            binding.more.setText(
                String.format(
                    "%d followers - %d tracks",
                    playlist.followers.total,
                    playlist.tracks.total
                )
            )
        }
    }

    private fun initTrack() {
        viewModel.trackList.observe(viewLifecycleOwner) { track ->
            trackAdapter.setData(track)
            trackAdapter.notifyDataSetChanged()
        }
        trackAdapter = PlaylistTrackAdapter(mutableListOf()) { track ->
            viewModel.setLyric(track.track.id)
            SharePref.setImageUrl(
                requireActivity().application,
                track.track.album.images.get(0).url
            )
            sharedViewModel.hideMiniPlayer()
            findNavController().navigate(
                PlaylistFragmentDirections.actionPlaylistFragmentToPlaySongFragment(
                    track.track.id
                )
            )
        }
        binding.recyclerView.adapter = trackAdapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val space = resources.getDimensionPixelSize(R.dimen.space_song)
        binding.recyclerView.addItemDecoration(SpacingItem(space))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}