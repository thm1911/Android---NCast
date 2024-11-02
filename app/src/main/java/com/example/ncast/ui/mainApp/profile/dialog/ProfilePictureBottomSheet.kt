package com.example.ncast.ui.mainApp.profile.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.ncast.R
import com.example.ncast.databinding.DialogChangePictureProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfilePictureBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogChangePictureProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageUrl: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogChangePictureProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.save.setOnClickListener {
            saveProfilePicture()
        }

        binding.cancel.setOnClickListener {
            dismiss()
        }
    }

    private fun saveProfilePicture() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("user").child(userId!!)

        userRef.child("imageUrl").setValue(imageUrl).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                findNavController().popBackStack()
                dismiss()
            }
        }


    }

    fun setImageUrl(url: String) {
        imageUrl = url
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
