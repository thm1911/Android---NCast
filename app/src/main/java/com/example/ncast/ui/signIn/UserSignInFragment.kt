package com.example.ncast.ui.signIn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.ncast.R
import com.example.ncast.databinding.FragmentUserSignInBinding
import com.example.ncast.utils.SharePref.SharePref

class UserSignInFragment : Fragment() {
    private var _binding: FragmentUserSignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserSignInViewModel by viewModels {
        UserSignInViewModel.UserSignInViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserSignInBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.userSignInFragment, true)
            .setPopUpTo(R.id.createAccountFragment, true)
            .build()
        binding.signIn.setOnClickListener {
            checkUser { check ->
                if(check){
                    viewModel.getUserIdFromUsername(binding.username.text.toString()){id ->
                        SharePref.setUserIdToSharePref(requireActivity().application, id)
                    }
                    findNavController().navigate(UserSignInFragmentDirections.actionUserSignInFragmentToMainAppFragment(), navOptions)

                }
            }
        }
    }

    private fun checkUser(callback: (Boolean) -> Unit){
        val username = binding.username.text.toString()
        val password = binding.password.text.toString()

        if(username.isNullOrEmpty()){
            binding.usernameLayout.helperText = "Cannot be left blank"
            callback(false)
            return
        }

        viewModel.checkUserSignIn(username, password){check ->
            if(check){
                callback(true)
                binding.passwordLayout.helperText = ""
                binding.usernameLayout.helperText = ""
            }
            else{
                binding.usernameLayout.helperText = ""
                binding.passwordLayout.helperText = "Password is incorrect"
                callback(false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}