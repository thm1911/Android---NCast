package com.example.ncast.ui.mainApp.playSong

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ncast.R
import com.example.ncast.databinding.FragmentLyricTrackBinding
import com.example.ncast.utils.SharePref

class LyricTrackFragment : Fragment() {
    private var _binding: FragmentLyricTrackBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLyricTrackBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lyric.setText(SharePref.getLyric(requireActivity().application))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}