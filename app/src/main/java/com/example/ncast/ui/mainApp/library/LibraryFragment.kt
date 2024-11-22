package com.example.ncast.ui.mainApp.library

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ncast.ui.mainApp.library.yourPlaylist.AddNewPlaylistFragment
import com.example.ncast.R
import com.example.ncast.SpacingItem
import com.example.ncast.adapter.recycleViewAdapterLibrary.YourPlaylistAdapter
import com.example.ncast.databinding.FragmentLibraryBinding
import com.example.ncast.model.yourPlaylist.YourPlaylist
import com.example.ncast.utils.Url
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var adapter: YourPlaylistAdapter
    private val viewModel: LibraryViewModel by viewModels {
        LibraryViewModel.LibraryViewModelFactory()
    }

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

        bottomNav = requireActivity().findViewById(R.id.bottomNavigation)
        bottomNav.visibility = View.VISIBLE

        auth = FirebaseAuth.getInstance()
        loadProfileImage()
        initYourPlaylist()

        binding.addNewPlayist.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("user").child(userId!!)
            userRef.child("playlistImageUrl").setValue(Url.IMAGEPLAYLIST.url)
            findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToAddNewPlaylistFragment())
            bottomNav.visibility = View.GONE
        }

        binding.yourLikedSongs.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_favoriteSongsPlaylistFragment)
            bottomNav.visibility = View.GONE
        }

        val space = resources.getDimensionPixelSize(R.dimen.space)
        binding.recycleYourPlaylist.addItemDecoration(SpacingItem(space))

    }

    private fun initYourPlaylist() {
        adapter = YourPlaylistAdapter(mutableListOf(),
            onClick = { playlist ->
                bottomNav.visibility = View.GONE
                val name = playlist.name
                findNavController().navigate(
                    LibraryFragmentDirections.actionLibraryFragmentToYourPlaylistFragment(name)
                )
            },
            onDelete = { playlist ->
                Log.d("test", "delete")
                showNoti(playlist.name)
            })
        viewModel.loadPlaylist()
        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            adapter.setData(playlist)
        }
        binding.recycleYourPlaylist.adapter = adapter
        binding.recycleYourPlaylist.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun loadProfileImage() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("user").child(userId)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(data: DataSnapshot) {
                    val imageUrl = data.child("imageUrl").getValue(String::class.java)
                    if (imageUrl != null) {
                        if (isAdded) {
                            Glide.with(this@LibraryFragment)
                                .load(imageUrl)
                                .into(binding.avt)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    private fun showNoti(name: String){
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Playlist?")
            .setMessage("Are you sure you want to delete?")
            .setNegativeButton("Yes"){dialog, _ ->
                deletePlaylist(name)
                dialog.dismiss()
            }
            .setPositiveButton("No"){dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deletePlaylist(name: String){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val playlistRef = FirebaseDatabase.getInstance().getReference("user")
            .child(userId!!)
            .child("playlist")
            .child(name)

        playlistRef.removeValue()
        initYourPlaylist()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
