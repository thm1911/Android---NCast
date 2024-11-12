package com.example.ncast.ui.mainApp.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ncast.R
import com.example.ncast.SpacingItem
import com.example.ncast.adapter.recycleViewAdapterLibrary.YourPlaylistAdapter
import com.example.ncast.databinding.FragmentLibraryBinding
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

        binding.yourLikedSongs.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_favoriteSongsPlaylistFragment)
            bottomNav.visibility = View.GONE
        }

        binding.recycleYourPlaylist.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapterUpdate = YourPlaylistAdapter()
        binding.recycleYourPlaylist.adapter = adapterUpdate

        val space = resources.getDimensionPixelSize(R.dimen.space)
        binding.recycleYourPlaylist.addItemDecoration(SpacingItem(space))

        loadProfileImage()
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
