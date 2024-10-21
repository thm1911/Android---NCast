package com.example.ncast.ui.signUp.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ncast.R
import com.example.ncast.databinding.FragmentUserSignUpBinding
import com.example.ncast.model.User
import com.example.ncast.utils.SharePref.SharePref

class UserSignUpFragment : Fragment() {
    private var _binding: FragmentUserSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserSignUpViewModel by viewModels {
        UserSignUpViewModel.UserSignUpViewModelFactory(requireActivity().application)
    }
    private val args: UserSignUpFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = args.email
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.userSignUpFragment, true)
            .setPopUpTo(R.id.emailSignUpFragment, true)
            .setPopUpTo(R.id.createAccountFragment, true)
            .build()

        binding.signUp.setOnClickListener {
            checkUser { check ->
                if(check){
                    val user = User(0, email, binding.username.text.toString(), binding.password.text.toString())
                    viewModel.insertUser(user){id ->
                        SharePref.setUserIdToSharePref(requireActivity().application, id)
                    }
                    findNavController().navigate(UserSignUpFragmentDirections.actionUserSignUpFragmentToMainAppFragment(), navOptions)
                }
            }
        }

    }

    private fun checkUser(callback: (Boolean) -> Unit){
        val username = binding.username.text.toString()
        val password = binding.password.text.toString()
        val cfPassword = binding.cfPassword.text.toString()

        if(username.isNullOrEmpty()){
            binding.usernameLayout.helperText = "Cannot be left blank"
            binding.username.setBackgroundResource(R.drawable.input_error)
            callback(false)
            return
        }
        else if(password.isNullOrEmpty()){
            binding.usernameLayout.helperText = ""
            binding.passwordLayout.helperText = "Cannot be left blank"
            binding.password.setBackgroundResource(R.drawable.input_error)
            binding.username.setBackgroundResource(R.drawable.input_text)
            callback(false)
            return
        }
        else if(cfPassword != password){
            binding.usernameLayout.helperText = ""
            binding.passwordLayout.helperText = ""
            binding.cfPasswordLayout.helperText = "Confirm password is incorrect"
            binding.cfPassword.setBackgroundResource(R.drawable.input_error)
            binding.password.setBackgroundResource(R.drawable.input_text)
            binding.username.setBackgroundResource(R.drawable.input_text)
            callback(false)
            return
        }

        viewModel.isUsernameExist(username){exist ->
            if(exist){
                binding.passwordLayout.helperText = ""
                binding.cfPasswordLayout.helperText = ""
                binding.usernameLayout.helperText = "Username already exists"
                binding.username.setBackgroundResource(R.drawable.input_error)
                binding.password.setBackgroundResource(R.drawable.input_text)
                binding.cfPassword.setBackgroundResource(R.drawable.input_text)
                callback(false)
            }
            else{
                binding.passwordLayout.helperText = ""
                binding.cfPasswordLayout.helperText = ""
                binding.usernameLayout.helperText = ""

                binding.cfPassword.setBackgroundResource(R.drawable.input_text)
                binding.password.setBackgroundResource(R.drawable.input_text)
                binding.username.setBackgroundResource(R.drawable.input_text)
                callback(true)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}