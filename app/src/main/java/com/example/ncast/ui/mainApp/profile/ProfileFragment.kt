package com.example.ncast.ui.mainApp.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.ncast.R
import com.example.ncast.databinding.FragmentProfileBinding
import com.example.ncast.model.User
import com.example.ncast.ui.mainApp.profile.dialog.ProfilePictureBottomSheet
import com.example.ncast.utils.SharePref
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.bumptech.glide.Glide
import com.example.ncast.ui.mainApp.profile.ProfileFragmentDirections

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.visibility = View.VISIBLE

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        init(userId!!)

        // Lắng nghe kết quả từ ProfilePictureBottomSheet
        setFragmentResultListener("requestKey") { _, bundle ->
            val imageUrl = bundle.getString("imageUrl")
            updateProfileImage(imageUrl)
        }

        binding.changeAvtLayout.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_chooseAppProfileFragment)
            bottomNav.visibility = View.GONE
        }

        binding.changePassword.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToChangePasswordFragment())
            bottomNav.visibility = View.GONE
        }

        binding.about.setOnClickListener {
            bottomNav.visibility = View.GONE
        }

        binding.logout.setOnClickListener {
            auth.signOut()

            SharePref.setUserLoginState(requireActivity().application, false)

            val navHostFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController

            navController.popBackStack()
            navController.navigate(R.id.createAccountFragment)
        }
    }

    private fun init(userId: String) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("user").child(userId)

        userRef.get().addOnSuccessListener { data ->
            val user = data.getValue(User::class.java)
            if (user != null) {
                binding.email.text = user.email
                binding.username.text = user.username
                user.imageUrl?.let { imageUrl ->
                    Glide.with(this)
                        .load(imageUrl)
                        .into(binding.imageAvt)
                }
            }
        }
    }

    private fun updateProfileImage(imageUrl: String?) {
        imageUrl?.let {
            Glide.with(this)
                .load(it)
                .into(binding.imageAvt)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
