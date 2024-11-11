package com.example.ncast.ui.mainApp.playSong

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ncast.R
import com.example.ncast.adapter.viewPagerAdapter.TrackAdapter
import com.example.ncast.databinding.FragmentPlaySongBinding
import com.example.ncast.model.SpotifyService
import com.example.ncast.utils.Track
import com.example.ncast.utils.Url
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.RepeatMode
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.common.io.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlaySongFragment : Fragment() {
    private var _binding: FragmentPlaySongBinding? = null
    private val binding get() = _binding!!
    private lateinit var spotifyService: SpotifyService
    private val args: PlaySongFragmentArgs by navArgs()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var handle: Handler
    private var runable: Runnable? = null
    private var repeatMode = Player.REPEAT_MODE_OFF
    private lateinit var urlMp3: String
    private lateinit var nameTrack: String
    private val viewModel: PlaySongViewModel by viewModels {
        PlaySongViewModel.PlaySongViewModelFactory(requireActivity().application, spotifyService)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaySongBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.visibility = View.GONE

        val idTrack = args.idTrack
        spotifyService = Retrofit.Builder()
            .baseUrl(Url.SPOTIFY.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyService::class.java)

        viewModel.loadTrack(idTrack)
        exoPlayer = ExoPlayer.Builder(requireContext()).build()
        handle = Handler(Looper.getMainLooper())
        setData()

        checkFavouriteStatus(args.idTrack)
        binding.favourite.setOnClickListener {
            toggleFavouriteStatus(args.idTrack)
        }

        // Dừng/tiếp tục phát nhạc
        binding.playPause.setOnClickListener {
            exoPlayer.playWhenReady = !exoPlayer.playWhenReady
        }

        // Lặp lại
        binding.repeat.setOnClickListener {
            setupRepeat()
        }

        // Tua lại 5s
        binding.replay.setOnClickListener {
            setupReplay()
        }

        // Tua tiếp 5s
        binding.forward.setOnClickListener {
            setupForward()
        }

        // popup Menu
        binding.menu.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

            val downLoadIcon = popupMenu.menu.findItem(R.id.download)
            if (urlMp3.isNullOrEmpty()) {
                downLoadIcon.icon = resources.getDrawable(R.drawable.ic_not_download)
                downLoadIcon.isEnabled = true
            }

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.add_playlist -> {
                        true
                    }

                    R.id.download -> {
                        if (urlMp3.isNullOrEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "Preview is unavailable. You don't download",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else downloadTrack()
                        true
                    }

                    else -> false
                }
            }
            popupMenu.setForceShowIcon(true)
            popupMenu.show()
        }

        // Thoát khỏi chế độ phát nhạc
        binding.gone.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun setData() {
        viewModel.track.observe(viewLifecycleOwner) { track ->
            Track.getUrlFromDatabase(track.id) { url ->
                if (url.isNullOrEmpty()) {
                    if (track.preview_url.isNullOrEmpty()) {
                        urlMp3 = ""
                        Toast.makeText(
                            requireContext(),
                            "Previews is unavailable",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        urlMp3 = track.preview_url
                    }
                } else urlMp3 = url

                nameTrack = track.name
                exoPlayer.addListener(object : Player.Listener {
                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        if (playbackState == Player.STATE_READY && playWhenReady) {
                            binding.playPause.setBackgroundResource(R.drawable.ic_play)
                            binding.name.setText(track.name)
                            binding.nameTrack.setText(track.name)
                            binding.artist.setText(track.artists.get(0).name)
                            binding.seekBar.max = exoPlayer.duration.toInt()
                            binding.duration.setText(formatTime(exoPlayer.duration.toInt()))
                            setupSeekBar()
                        } else if (playbackState == Player.STATE_READY && !playWhenReady) {
                            binding.playPause.setBackgroundResource(R.drawable.ic_pause)
                            binding.name.setText(track.name)
                            binding.nameTrack.setText(track.name)
                            binding.artist.setText(track.artists.get(0).name)
                            binding.seekBar.max = exoPlayer.duration.toInt()
                            binding.duration.setText(formatTime(exoPlayer.duration.toInt()))
                        }

//                        else if(playbackState == Player.STATE_ENDED){
//                            findNavController().popBackStack()
//                        }
                    }

                })

                val mediaItem = MediaItem.fromUri(urlMp3)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()

                // update seekBar khi kéo
                binding.seekBar.setOnSeekBarChangeListener(object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (fromUser) exoPlayer.seekTo(progress.toLong())
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {}
                    override fun onStopTrackingTouch(p0: SeekBar?) {}
                })

            }
        }

        // viewPager + tabLayout để hiển thị ảnh, lyric
        trackAdapter = TrackAdapter(parentFragmentManager, lifecycle)
        binding.viewPager.adapter = trackAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->

        }.attach()
    }

    // Cập nhât seekBar
    private fun setupSeekBar() {
        runable = Runnable {
            if (exoPlayer.isPlaying) {
                binding.seekBar.progress = exoPlayer.currentPosition.toInt()
                binding.curPlay.setText(formatTime(exoPlayer.currentPosition.toInt()))
                handle.postDelayed(runable!!, 1000)
            }
        }
        handle.postDelayed(runable!!, 0)
    }

    // Chuyển đổi duration từ milliseconds -> dạng 00:00
    private fun formatTime(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun setupRepeat() {
        repeatMode = when (repeatMode) {
            Player.REPEAT_MODE_OFF -> {
                binding.repeat.setBackgroundResource(R.drawable.ic_repeat_one)
                exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                Player.REPEAT_MODE_ONE
            }

            Player.REPEAT_MODE_ONE -> {
                binding.repeat.setBackgroundResource(R.drawable.ic_repeat_off)
                exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
                Player.REPEAT_MODE_OFF
            }

            else -> Player.REPEAT_MODE_OFF
        }
    }

    //Tua lại 5s
    private fun setupReplay() {
        val curPos = exoPlayer.currentPosition
        exoPlayer.seekTo(maxOf(curPos - 5000, 0))
    }

    //Tua tiếp 5s
    private fun setupForward() {
        val curPos = exoPlayer.currentPosition
        exoPlayer.seekTo(minOf(curPos + 5000, exoPlayer.duration))
        exoPlayer.playWhenReady = true
    }

    private fun downloadTrack() {
        val request = DownloadManager.Request(Uri.parse(urlMp3))
            .setTitle("Downloading ${nameTrack}.mp3")
            .setDescription("Downloading file...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${nameTrack}.mp3")

        val downloadManager =
            requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    private fun toggleFavouriteStatus(trackId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val favouriteTracksRef = database.getReference("user/$userId/favourite_tracks")

        favouriteTracksRef.child(trackId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Xóa bài hát khỏi danh sách yêu thích
                    snapshot.ref.removeValue()
                    binding.favourite.setBackgroundResource(R.drawable.ic_unfavourite)
                } else {
                    // Lấy thứ tự hiện có cao nhất trong danh sách yêu thích
                    favouriteTracksRef.orderByChild("order").limitToLast(1)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(orderSnapshot: DataSnapshot) {
                                // Xác định thứ tự cho bài hát mới
                                var nextOrder = 1
                                if (orderSnapshot.exists()) {
                                    val lastOrder = orderSnapshot.children.first()
                                        .child("order").getValue(Int::class.java) ?: 0
                                    nextOrder = lastOrder + 1
                                }

                                // Thêm bài hát với thứ tự mới
                                val newFavourite = mapOf(
                                    "id" to trackId,
                                    "order" to nextOrder
                                )
                                favouriteTracksRef.child(trackId).setValue(newFavourite)
                                binding.favourite.setBackgroundResource(R.drawable.ic_favourite)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Xử lý lỗi nếu cần
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý lỗi nếu cần
            }
        })
    }



    private fun checkFavouriteStatus(trackId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val favouriteTracksRef = database.getReference("user/$userId/favourite_tracks")

        favouriteTracksRef.child(trackId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.favourite.setBackgroundResource(R.drawable.ic_favourite)
                } else {
                    binding.favourite.setBackgroundResource(R.drawable.ic_unfavourite)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (!urlMp3.isNullOrEmpty()) {
            handle.removeCallbacks(runable!!)
            exoPlayer.release()
        }
        _binding = null
    }

}