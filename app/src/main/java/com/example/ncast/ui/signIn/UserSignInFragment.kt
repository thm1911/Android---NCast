package com.example.ncast.ui.signIn

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.ncast.R
import com.example.ncast.databinding.FragmentUserSignInBinding
import com.example.ncast.utils.SharePref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserSignInFragment : Fragment() {
    private var _binding: FragmentUserSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserSignInBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val noSpaceFilter =
            InputFilter { source, _, _, _, _, _ -> //source, start, end, dest, dstart, dend
                if (source == " ") "" else null
            }

        binding.email.filters = arrayOf(noSpaceFilter, InputFilter.LengthFilter(25))
        binding.password.filters = arrayOf(noSpaceFilter, InputFilter.LengthFilter(25))

        binding.email.addTextChangedListener {
            if (it?.length == 25) {
                binding.emailLayout.helperText = "Email maximum 25 characters"
                binding.email.setBackgroundResource(R.drawable.input_error)
            } else {
                binding.emailLayout.helperText = ""
                binding.email.setBackgroundResource(R.drawable.input_text)
            }
        }

        binding.password.addTextChangedListener {
            if (it?.length == 25) {
                binding.passwordLayout.helperText = "Password maximum 25 characters"
                binding.password.setBackgroundResource(R.drawable.input_error)
            } else {
                binding.passwordLayout.helperText = ""
                binding.password.setBackgroundResource(R.drawable.input_text)
            }
        }

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.userSignInFragment, true)
            .setPopUpTo(R.id.createAccountFragment, true)
            .build()

        binding.signIn.setOnClickListener {
            checkUser { check ->
                if (check) {
                    val userId = auth.currentUser?.uid
                    val password = binding.password.text.toString()
                    updatePassToDatabase(userId!!, password)

                    SharePref.setUserLoginState(requireActivity().application, true)

                    findNavController().navigate(
                        UserSignInFragmentDirections.actionUserSignInFragmentToMainAppFragment(),
                        navOptions
                    )
                }
            }
        }

        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(UserSignInFragmentDirections.actionUserSignInFragmentToForgotPasswordFragment())
        }
        binding.signUp.setOnClickListener {
            findNavController().navigate(UserSignInFragmentDirections.actionUserSignInFragmentToUserSignUpFragment())
        }
    }

    private fun checkUser(callback: (Boolean) -> Unit) {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (email.isNullOrEmpty()) {
            binding.emailLayout.helperText = "Cannot be left blank"
            binding.email.setBackgroundResource(R.drawable.input_error)
            callback(false)
            return
        } else if (password.isNullOrEmpty()) {
            binding.passwordLayout.helperText = "Cannot be left blank"
            binding.password.setBackgroundResource(R.drawable.input_error)
            binding.emailLayout.helperText = ""
            binding.email.setBackgroundResource(R.drawable.input_text)
            callback(false)
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true)
                    binding.passwordLayout.helperText = ""
                    binding.emailLayout.helperText = ""
                    binding.password.setBackgroundResource(R.drawable.input_text)
                    binding.email.setBackgroundResource(R.drawable.input_text)
                } else {
                    binding.emailLayout.helperText = ""
                    binding.passwordLayout.helperText = "Password or email is incorrect"
                    binding.password.setBackgroundResource(R.drawable.input_error)
                    binding.email.setBackgroundResource(R.drawable.input_text)
                    callback(false)
                }
            }
    }

    private fun updatePassToDatabase(userId: String, pass: String) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("user").child(userId)
        userRef.child("password").setValue(pass)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}