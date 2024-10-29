package com.example.ncast.ui.mainApp.home.newAlbumRelease

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ncast.adapter.recyclerViewAdapterHome.NewAlbumReleaseAdapter
import com.example.ncast.database.SpotifyService
import com.example.ncast.databinding.FragmentNewReleaseAlbumBinding
import com.example.ncast.utils.Url
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewReleaseAlbumFragment : Fragment() {
    private var _binding: FragmentNewReleaseAlbumBinding? = null
    private val binding get() = _binding!!
    private lateinit var newAlbumReleaseAdapter: NewAlbumReleaseAdapter
    private lateinit var spotifyService: SpotifyService
    private val viewModel: NewReleaseAlbumViewModel by viewModels {
        NewReleaseAlbumViewModel.NewReleaseAlbumViewModelFactory(spotifyService)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewReleaseAlbumBinding.inflate(layoutInflater, container, false)
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
        viewModel.loadAlbums()

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }


    }


    private fun initRecyclerView(){
        val recyclerView = binding.recyclerViewNewAlbumRelease
        newAlbumReleaseAdapter = NewAlbumReleaseAdapter(mutableListOf()){album ->
            findNavController().navigate(NewReleaseAlbumFragmentDirections.actionNewReleaseAlbumFragmentToAlbumInforFragment(album.id))
        }

        viewModel.newAlbumReleaseList.observe(viewLifecycleOwner){albums ->
            newAlbumReleaseAdapter.setData(albums)
            newAlbumReleaseAdapter.notifyDataSetChanged()
        }
        recyclerView.adapter = newAlbumReleaseAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        newAlbumReleaseAdapter.showAllAlbum(true)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}