package com.example.ncast.ui.mainApp.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.ncast.R
import com.example.ncast.SpacingItem
import com.example.ncast.adapter.recycleViewAdapterSearch.DiscoveriesAdapter
import com.example.ncast.adapter.recycleViewAdapterSearch.UpdateEveryHourAdapter
import com.example.ncast.databinding.FragmentSearchBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth=FirebaseAuth.getInstance()

        //Update Search Songs
        binding.recyclerUpdateEveryHour.layoutManager=GridLayoutManager(requireContext(),2)
        val adapterUpdate=UpdateEveryHourAdapter()
        binding.recyclerUpdateEveryHour.adapter=adapterUpdate

        //Discovery
        binding.recyclerDiscoveries.layoutManager=GridLayoutManager(requireContext(),2)
        val adapterDiscovery=DiscoveriesAdapter()
        binding.recyclerDiscoveries.adapter=adapterDiscovery

        //Adding spacing
        val space=resources.getDimensionPixelSize(R.dimen.space)
        binding.recyclerUpdateEveryHour.addItemDecoration(SpacingItem(space))

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
                            Glide.with(this@SearchFragment)
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
