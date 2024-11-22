package com.example.ncast.ui.mainApp.playSong

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import com.bumptech.glide.request.transition.Transition
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.ncast.MainActivity
import com.example.ncast.R
import com.example.ncast.adapter.recycleViewAdapterLibrary.FavoriteTrackAdapter
import com.example.ncast.ui.mainApp.SharedViewModel
import com.example.ncast.adapter.viewPagerAdapter.TrackAdapter
import com.example.ncast.databinding.FragmentPlaySongBinding
import com.example.ncast.model.SpotifyService
import com.example.ncast.ui.mainApp.library.favoriteSong.FavoriteSongsViewModel
import com.example.ncast.ui.mainApp.library.yourPlaylist.PickPlaylistDialogFragment
import com.example.ncast.utils.Track
import com.example.ncast.utils.Url
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory

class PlaySongFragment() : Fragment() {
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
    private val favouriteSongViewModel: FavoriteSongsViewModel by viewModels{
        FavoriteSongsViewModel.FavoriteSongsViewModelFactory(requireActivity().application, spotifyService)
    }
    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaySongBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val idTrack: String = args.idTrack

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.visibility = View.GONE

        sharedViewModel.hideMiniPlayer()

        spotifyService = Retrofit.Builder()
            .baseUrl(Url.SPOTIFY.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyService::class.java)

        viewModel.loadTrack(idTrack)
        exoPlayer = (activity as MainActivity).getExoPlayer()
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
                        val pickPlaylist = PickPlaylistDialogFragment()
                        pickPlaylist.setIdTrack(idTrack)
                        pickPlaylist.show(parentFragmentManager, pickPlaylist.tag)
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
//            popupMenu.setForceShowIcon(true)
            popupMenu.show()
        }

        // Thoát khỏi chế độ phát nhạc
        binding.gone.setOnClickListener {
            if (urlMp3.isNullOrEmpty()) {
                findNavController().popBackStack()
            } else {
                sharedViewModel.showMiniPlayer()
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            findNavController().popBackStack()
            sharedViewModel.showMiniPlayer()
        }

    }

    private fun setData() {
        viewModel.track.observe(viewLifecycleOwner) { track ->

            val albumArtUrl = track.album.images[0].url // URL ảnh bìa bài hát
            applyGradientBackground(albumArtUrl) // Áp dụng gradient nền

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

                sharedViewModel.setIdTrack(track.id)
                sharedViewModel.setNameTrack(track.name)
                sharedViewModel.setArtistTrack(track.artists.get(0).name)

                nameTrack = track.name
                exoPlayer.addListener(object : Player.Listener {
                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        if (playbackState == Player.STATE_READY && playWhenReady && _binding != null) {
                            binding.playPause.setBackgroundResource(R.drawable.ic_play)
                            binding.name.setText(track.name)
                            binding.nameTrack.setText(track.name)
                            binding.artist.text = track.artists.joinToString(", ") { it.name }

                            binding.seekBar.max = exoPlayer.duration.toInt()
                            binding.duration.setText(formatTime(exoPlayer.duration.toInt()))
                            setupSeekBar()
                        } else if (playbackState == Player.STATE_READY && !playWhenReady && _binding != null) {
                            binding.playPause.setBackgroundResource(R.drawable.ic_pause)
                            binding.name.setText(track.name)
                            binding.nameTrack.setText(track.name)
                            binding.artist.text = track.artists.joinToString(", ") { it.name }

                            binding.seekBar.max = exoPlayer.duration.toInt()
                            binding.duration.setText(formatTime(exoPlayer.duration.toInt()))
                        }
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

        favouriteTracksRef.get().addOnSuccessListener { snapshot ->
            val favouriteList = mutableListOf<String>()
            snapshot.children.forEach { child ->
                child.getValue(String::class.java)?.let { favouriteList.add(it) }
            }

            if (favouriteList.contains(trackId)) {
                // Xóa bài hát khỏi danh sách yêu thích
                favouriteList.remove(trackId)
                binding.favourite.setBackgroundResource(R.drawable.ic_unfavourite)
            } else {
                // Thêm bài hát vào đầu danh sách yêu thích
                favouriteList.add(0, trackId)
                binding.favourite.setBackgroundResource(R.drawable.ic_favourite)
            }

            // Lưu danh sách mới vào Firebase
            favouriteTracksRef.setValue(favouriteList)
        }
    }


    private fun checkFavouriteStatus(trackId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val favouriteTracksRef = database.getReference("user/$userId/favourite_tracks")

        favouriteTracksRef.get().addOnSuccessListener { snapshot ->
            val favoriteList = snapshot.children.mapNotNull { it.getValue(String::class.java) }
            if (favoriteList.contains(trackId)) {
                binding.favourite.setBackgroundResource(R.drawable.ic_favourite)
            } else {
                binding.favourite.setBackgroundResource(R.drawable.ic_unfavourite)
            }
        }
    }

    private fun applyGradientBackground(imageUrl: String) {
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // Sử dụng Palette
                    Palette.from(resource).generate { palette ->
                        val vibrantColor = palette?.getVibrantColor(0xFF000000.toInt()) ?: 0xFF000000.toInt()
                        sharedViewModel.setDominantColor(vibrantColor)

                        val transparentColor = adjustAlpha(vibrantColor, 0.0f) // Làm màu trong suốt

                        val gradientDrawable = GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            intArrayOf(vibrantColor, transparentColor)
                        )

                        gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT

                        // gradient ben tren
                        binding.gradientOverlay.background = gradientDrawable
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    // giam opacity
    private fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = (color shr 24 and 0xFF) * factor
        val red = color shr 16 and 0xFF
        val green = color shr 8 and 0xFF
        val blue = color and 0xFF
        return (alpha.toInt() shl 24) or (red shl 16) or (green shl 8) or blue
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!urlMp3.isNullOrEmpty()) {
            handle.removeCallbacks(runable!!)
        }
        _binding = null
    }

}