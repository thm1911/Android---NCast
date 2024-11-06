package com.example.ncast.ui.mainApp.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ncast.R
import com.example.ncast.adapter.recycleViewAdapterProfile.ChoosePictureAdapter
import com.example.ncast.databinding.FragmentChooseAppProfileBinding
import com.example.ncast.ui.mainApp.profile.dialog.ProfilePictureBottomSheet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ChooseAppProfileFragment : Fragment() {
    private var _binding: FragmentChooseAppProfileBinding? = null
    private val binding get() = _binding!!
    private val imageUrls = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseAppProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewChoosePiture.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = ChoosePictureAdapter(imageUrls) { selectedImageUrl ->
            showProfilePictureBottomSheet(selectedImageUrl)
        }
        binding.recyclerViewChoosePiture.adapter = adapter

        // Lấy ảnh từ Firebase Storage
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val storageRef = FirebaseStorage.getInstance().reference.child("picture_avt/$userId")
            storageRef.listAll()
                .addOnSuccessListener { listResult ->
                    if (listResult.items.isNotEmpty()) {
                        listResult.items.forEach { item ->
                            item.downloadUrl.addOnSuccessListener { uri ->
                                imageUrls.add(uri.toString())
                                adapter.notifyItemInserted(imageUrls.size - 1)
                            }
                        }
                    }
                }

            binding.back.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            binding.addPhoto.setOnClickListener {
                openGallery()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK) {
            data?.data?.let { uri ->
                uploadImageToFirebase(uri)
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val storageRef = FirebaseStorage.getInstance().reference
                .child("picture_avt/$userId/${UUID.randomUUID()}.jpg")

            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        addImageToRecyclerView(imageUrl) // Thêm ảnh vào đầu danh sách
                    }
                }
        }
    }

    private fun addImageToRecyclerView(imageUrl: String) {
        imageUrls.add(0, imageUrl)
        binding.recyclerViewChoosePiture.adapter?.notifyItemInserted(0)
    }

    private fun showProfilePictureBottomSheet(imageUrl: String) {
        val bottomSheet = ProfilePictureBottomSheet()
        bottomSheet.setImageUrl(imageUrl)
        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }
}
