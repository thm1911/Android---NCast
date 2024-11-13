package com.example.ncast.ui.signUp

import android.os.Bundle
import android.text.InputFilter
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.ncast.R
import com.example.ncast.databinding.FragmentUserSignUpBinding
import com.example.ncast.model.User
import com.example.ncast.utils.SharePref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserSignUpFragment : Fragment() {
    private var _binding: FragmentUserSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val noSpaceFilter = InputFilter { source, _, _, _, _, _ ->
            if (source == " ") "" else null
        }

        binding.email.filters = arrayOf(noSpaceFilter, InputFilter.LengthFilter(25))
        binding.password.filters = arrayOf(noSpaceFilter, InputFilter.LengthFilter(25))
        binding.cfPassword.filters = arrayOf(noSpaceFilter, InputFilter.LengthFilter(25))
        binding.username.filters = arrayOf(noSpaceFilter, InputFilter.LengthFilter(25))

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
        binding.cfPassword.addTextChangedListener {
            if (it?.length == 25) {
                binding.cfPasswordLayout.helperText = "Password maximum 25 characters"
                binding.cfPassword.setBackgroundResource(R.drawable.input_error)
            } else {
                binding.cfPasswordLayout.helperText = ""
                binding.cfPassword.setBackgroundResource(R.drawable.input_text)
            }
        }
        binding.username.addTextChangedListener {
            if (it?.length == 25) {
                binding.usernameLayout.helperText = "Email maximum 25 characters"
                binding.username.setBackgroundResource(R.drawable.input_error)
            } else {
                binding.usernameLayout.helperText = ""
                binding.username.setBackgroundResource(R.drawable.input_text)
            }
        }

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.userSignUpFragment, true)
            .setPopUpTo(R.id.createAccountFragment, true)
            .build()

        binding.signUp.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val username = binding.username.text.toString()

            checkUser { check ->
                if (check) {
                    auth.createUserWithEmailAndPassword(
                        email,
                        password
                    )          //Dung Firebase authencation dang nhap/dang ki
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                val user = User(userId!!, email, username, password)

                                SharePref.setUserLoginState(requireActivity().application, true)

                                saveUserToDatabase(user) //Luu nguoi dung vao firebase realtime database
                                findNavController().navigate(
                                    UserSignUpFragmentDirections.actionUserSignUpFragmentToMainAppFragment(),
                                    navOptions
                                )
                            } else {
                                binding.emailLayout.helperText = "Email is exist"
                            }
                        }
                }
            }
        }


        binding.signIn.setOnClickListener {
            findNavController().navigate(UserSignUpFragmentDirections.actionUserSignUpFragmentToUserSignInFragment())
        }
    }

    private fun checkUser(callback: (Boolean) -> Unit) {
        val email = binding.email.text.toString()
        val username = binding.username.text.toString()
        val password = binding.password.text.toString()
        val cfPassword = binding.cfPassword.text.toString()

        if (email.isNullOrEmpty()) {
            binding.emailLayout.helperText = "Cannot be left blank"
            binding.email.setBackgroundResource(R.drawable.input_error)
            callback(false)
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.helperText = "Invalid email"
            binding.email.setBackgroundResource(R.drawable.input_error)
            callback(false)
            return
        } else if (username.isNullOrEmpty()) {
            binding.usernameLayout.helperText = "Cannot be left blank"
            binding.emailLayout.helperText = ""
            binding.username.setBackgroundResource(R.drawable.input_error)
            callback(false)
            return
        } else if (password.isNullOrEmpty()) {
            binding.emailLayout.helperText = ""
            binding.usernameLayout.helperText = ""
            binding.passwordLayout.helperText = "Cannot be left blank"
            binding.password.setBackgroundResource(R.drawable.input_error)
            binding.username.setBackgroundResource(R.drawable.input_text)
            callback(false)
            return
        } else if (password.length < 6) {
            binding.emailLayout.helperText = ""
            binding.usernameLayout.helperText = ""
            binding.passwordLayout.helperText = "Password must be at least 6 characters"
            binding.password.setBackgroundResource(R.drawable.input_error)
            binding.username.setBackgroundResource(R.drawable.input_text)
            callback(false)
            return
        } else if (cfPassword != password) {
            binding.emailLayout.helperText = ""
            binding.usernameLayout.helperText = ""
            binding.passwordLayout.helperText = ""
            binding.cfPasswordLayout.helperText = "Confirm password is incorrect"
            binding.cfPassword.setBackgroundResource(R.drawable.input_error)
            binding.password.setBackgroundResource(R.drawable.input_text)
            binding.username.setBackgroundResource(R.drawable.input_text)
            callback(false)
            return
        } else callback(true)

    }

    private fun saveUserToDatabase(user: User) {
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("user").child(user.userId)

        userRef.setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
            } else {
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}