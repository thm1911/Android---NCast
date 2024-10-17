package com.example.ncast.ui.signUp.email

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ncast.databinding.FragmentEmailSignUpBinding

class EmailSignUpFragment : Fragment() {
    private var _binding: FragmentEmailSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmailSignUpViewModel by viewModels {
        EmailSignUpViewModel.EmailSignUpViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmailSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.next.setOnClickListener {
            checkEmail { check ->
                if(check){
                    findNavController().navigate(EmailSignUpFragmentDirections.actionEmailSignUpFragmentToUserSignUpFragment(binding.email.text.toString()))
                }
            }
        }
    }

    private fun checkEmail(callback: (Boolean) -> Unit){
        val email = binding.email.text.toString()
        if (email.isNullOrEmpty()){
            binding.emailLayout.helperText = "Cannot be left blank"
            callback(false)
            return
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailLayout.helperText = "Invalid email"
            callback(false)
            return
        }

        viewModel.isEmailExist(email){exist ->
            if(exist){
                binding.emailLayout.helperText = "Email already exists"
                callback(false)
            }
            else callback(true)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}