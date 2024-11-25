package com.example.ncast.ui.mainApp.library.yourPlaylist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.ncast.R
import com.example.ncast.databinding.FragmentAddNewPlaylistBinding
import com.example.ncast.model.User
import com.example.ncast.model.yourPlaylist.YourPlaylist
import com.example.ncast.ui.mainApp.home.featuredPlaylist.PlaylistFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddNewPlaylistFragment : Fragment() {
    private var _binding: FragmentAddNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageUrl: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNewPlaylistBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updatePlaylistImage()
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.image.setOnClickListener {
            findNavController().navigate(AddNewPlaylistFragmentDirections.actionAddNewPlaylistFragmentToChooseAppProfileFragment("Picture Playlist"))
        }

        binding.selectImage.setOnClickListener {
            findNavController().navigate(AddNewPlaylistFragmentDirections.actionAddNewPlaylistFragmentToChooseAppProfileFragment("Picture Playlist"))
        }

        binding.cancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.save.setOnClickListener {
            check{ result ->
                if(result){
                    val playlist = YourPlaylist(imageUrl, binding.name.text.toString().trim())
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val database = FirebaseDatabase.getInstance()
                    val userRef = database.getReference("user").child(userId!!)
                    userRef.child("playlist").child(binding.name.text.toString().trim()).setValue(playlist)

                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun updatePlaylistImage(){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("user").child(userId!!)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                imageUrl = snapshot.child("playlistImageUrl").getValue(String::class.java) ?: ""
                if(!imageUrl.isNullOrEmpty()){
                    Glide.with(this@AddNewPlaylistFragment)
                        .load(imageUrl)
                        .into(binding.image)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun check(callback: (Boolean) -> Unit){
        val name = binding.name.text.toString().trim()
        if (name.isNullOrEmpty()){
            binding.nameLayout.helperText = "Please fill in all fields"
            binding.name.setBackgroundResource(R.drawable.input_error)
            callback(false)
            return
        }
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("user").child(userId!!)

        userRef.child("playlist").child(name).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()){
                binding.nameLayout.helperText = "Name is exist"
                binding.name.setBackgroundResource(R.drawable.input_error)
                callback(false)
            }
            else{
                binding.nameLayout.helperText = ""
                binding.name.setBackgroundResource(R.drawable.input_text)
                callback(true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}