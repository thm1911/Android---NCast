package com.example.ncast.ui.forgotPassword

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.ncast.R
import com.example.ncast.databinding.FragmentForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding.confirm.setOnClickListener {
            checkEmail { check ->
                val email = binding.email.text.toString().trim()
                if (check) {

                    auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Send email to get password successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().popBackStack()
                            findNavController().navigate(R.id.userSignInFragment)

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Send email failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun checkEmail(callback: (Boolean) -> Unit){
        val email = binding.email.text.toString()

        if(email.isNullOrEmpty()){
            binding.emailLayout.helperText = "Cannot be left blank"
            binding.email.setBackgroundResource(R.drawable.input_error)
            callback(false)
            return
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailLayout.helperText = "Invalid email"
            binding.email.setBackgroundResource(R.drawable.input_error)
            callback(false)
            return
        }
        else callback(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}