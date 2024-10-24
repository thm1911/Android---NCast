package com.example.ncast.ui.mainApp.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ncast.R
import com.example.ncast.SpacingItem
import com.example.ncast.adapter.recyckeViewAdapterLibrary.YourPlaylistAdapter
import com.example.ncast.adapter.recycleViewAdapterSearch.UpdateEveryHourAdapter
import com.example.ncast.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Your Playlist
        binding.recycleYourPlaylist.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapterUpdate = YourPlaylistAdapter()
        binding.recycleYourPlaylist.adapter = adapterUpdate

        // Adding spacing
        val space = resources.getDimensionPixelSize(R.dimen.space)
        binding.recycleYourPlaylist.addItemDecoration(SpacingItem(space))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
