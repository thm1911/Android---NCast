package com.example.ncast.ui.mainApp.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.ncast.R
import com.example.ncast.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModel.ProfileViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        binding.changePassword.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToChangePasswordFragment())
        }

        binding.logout.setOnClickListener {
            val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController

            navController.popBackStack()
            navController.navigate(R.id.createAccountFragment)
        }

    }

    private fun init(){
        viewModel.user.observe(viewLifecycleOwner){user ->
            binding.username.setText(user.username)
            binding.email.setText(user.email)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
