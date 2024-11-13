package com.example.ncast.ui.mainApp

import android.app.Activity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.ncast.MainActivity
import com.example.ncast.R
import com.example.ncast.databinding.FragmentMiniPlayerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player

class MiniPlayerFragment : Fragment() {
    private var _binding: FragmentMiniPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var exoPlayer: ExoPlayer
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMiniPlayerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exoPlayer = (activity as MainActivity).getExoPlayer()

        sharedViewModel.nameTrack.observe(viewLifecycleOwner){name ->
            binding.nameTrack.setText(name)
        }

        sharedViewModel.artistTrack.observe(viewLifecycleOwner){artist ->
            binding.artistTrack.setText(artist)
        }

        init()
    }

    private fun init(){
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_READY && playWhenReady && _binding != null) {
                    binding.playPauseHide.setBackgroundResource(R.drawable.ic_play)
                } else if (playbackState == Player.STATE_READY && !playWhenReady && _binding != null) {
                    binding.playPauseHide.setBackgroundResource(R.drawable.ic_pause)
                }
            }
        })
        if(binding != null){
            binding.playPauseHide.setOnClickListener {
                exoPlayer.playWhenReady = !exoPlayer.playWhenReady
            }
        }

        binding.close.setOnClickListener {
            exoPlayer.stop()
            sharedViewModel.hideMiniPlayer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}