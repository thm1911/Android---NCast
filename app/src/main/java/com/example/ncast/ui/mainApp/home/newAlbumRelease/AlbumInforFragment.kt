package com.example.ncast.ui.mainApp.home.newAlbumRelease

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ncast.R
import com.example.ncast.SpacingItem
import com.example.ncast.adapter.recyclerViewAdapterHome.SongAdapter
import com.example.ncast.model.SpotifyService
import com.example.ncast.databinding.FragmentAlbumInforBinding
import com.example.ncast.utils.SharePref
import com.example.ncast.utils.Url
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AlbumInforFragment : Fragment() {
    private var _binding: FragmentAlbumInforBinding? = null
    private val binding get() = _binding!!
    private val args: AlbumInforFragmentArgs by navArgs()
    private lateinit var spotifyService: SpotifyService
    private lateinit var songAdapter: SongAdapter
    private val viewModel: AlbumInforViewModel by viewModels {
        AlbumInforViewModel.AlbumInforViewModelFactory(spotifyService)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlbumInforBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spotifyService = Retrofit.Builder()
            .baseUrl(Url.SPOTIFY.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyService::class.java)

        val albumId = args.id
        viewModel.loadAlbums(albumId)
        viewModel.album.observe(viewLifecycleOwner){album ->
            Glide.with(binding.image.context)
                .load(album.images.get(0).url)
                .into(binding.image)

            binding.albumType.setText(album.album_type)
            binding.name.setText(album.name)
            binding.more.setText(String.format("%s - %d tracks", album.artists.get(0).name, album.total_tracks))
            SharePref.setImageUrl(requireActivity().application, album.images.get(0).url)
        }

        initSong()

        binding.close.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun initSong(){
        songAdapter = SongAdapter(mutableListOf()){song ->
            findNavController().navigate(AlbumInforFragmentDirections.actionAlbumInforFragmentToPlaySongFragment(song.id))
        }

        viewModel.songList.observe(viewLifecycleOwner){song ->
            songAdapter.setData(song)
        }
        binding.recyclerView.adapter = songAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val space = resources.getDimensionPixelSize(R.dimen.space_song)
        binding.recyclerView.addItemDecoration(SpacingItem(space))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}