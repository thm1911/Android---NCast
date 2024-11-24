package com.example.ncast.ui.mainApp.profile.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.ncast.databinding.DialogChangePictureProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PictureDialogFragment  : DialogFragment() {

    private var _binding: DialogChangePictureProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageUrl: String
    private lateinit var mes: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogChangePictureProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConfirm.setOnClickListener {
            if(mes.equals("Profile Avt")) saveProfilePicture()
            else if(mes.equals("Picture Playlist")) savePicturePlaylist()
        }

        binding.btnCancel.setOnClickListener {
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

    private fun savePicturePlaylist(){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("user").child(userId!!)
        userRef.child("playlistImageUrl").setValue(imageUrl).addOnCompleteListener{task ->
            if (task.isSuccessful){
                findNavController().popBackStack()
                dismiss()
            }
        }
    }

    fun setImageUrl(url: String) {
        imageUrl = url
    }

    fun setMes(mes: String){
        this.mes = mes
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
