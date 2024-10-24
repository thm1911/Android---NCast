package com.example.ncast.ui.mainApp.home

import com.example.ncast.adapter.recyclerViewAdapterHome.ContinueCategoryAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ncast.R
import com.example.ncast.SpacingItem
import com.example.ncast.adapter.recyclerViewAdapterHome.RecentMusicListeningAdapter
import com.example.ncast.adapter.recyclerViewAdapterHome.TopTrendingAdapter
import com.example.ncast.databinding.FragmentHomeBinding
import com.example.ncast.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Continue Listening
        binding.recyclerViewContinue.layoutManager = GridLayoutManager(requireContext(), 2) // cột
        val adapter = ContinueCategoryAdapter()
        binding.recyclerViewContinue.adapter = adapter

        // Top Trending
        binding.recyclerViewTopTrending.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapterTrending = TopTrendingAdapter()
        binding.recyclerViewTopTrending.adapter = adapterTrending

        // Recent Music Listening
        binding.recyclerViewRecentMusicListening.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapterRecentMusic = RecentMusicListeningAdapter()
        binding.recyclerViewRecentMusicListening.adapter = adapterRecentMusic

        // Adding spacing
        val space = resources.getDimensionPixelSize(R.dimen.space)
        binding.recyclerViewContinue.addItemDecoration(SpacingItem(space))

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        initUser(userId!!)
    }

    private fun initUser(userId: String){
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("user").child(userId)
        userRef.get().addOnSuccessListener { data ->
            val user = data.getValue(User::class.java)
            if (user != null){
                binding.name.text = user.username
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
