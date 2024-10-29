package com.example.ncast.ui.mainApp.profile.changePassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.ncast.R
import com.example.ncast.databinding.FragmentChangePasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChangePasswordFragment : Fragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()


        binding.save.setOnClickListener {
            check{check ->
                if(check){
                    findNavController().popBackStack()
                }
            }
        }

        binding.cancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.forgotPassword.setOnClickListener {
            auth.signOut()
            val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController

            navController.popBackStack()
            navController.navigate(R.id.forgotPasswordFragment)
        }
    }

    private fun check(callback: (Boolean) -> Unit){
        val user = auth.currentUser
        val newPass = binding.newPassword.text.toString()
        val cfNewPass = binding.cfNewPassword.text.toString()

        if(newPass.isNullOrEmpty()){
            binding.curPasswordLayout.helperText = ""
            binding.newPasswordLayout.helperText = "Cannot be left blank"
            binding.curPassword.setBackgroundResource(R.drawable.input_text)
            binding.newPassword.setBackgroundResource(R.drawable.input_error)
            callback(false)
            return
        }
        else if(newPass.length < 6){
            binding.curPasswordLayout.helperText = ""
            binding.newPasswordLayout.helperText = "Password must be at least 6 characters"
            binding.curPassword.setBackgroundResource(R.drawable.input_text)
            binding.newPassword.setBackgroundResource(R.drawable.input_error)
            callback(false)
            return
        }
        else if(cfNewPass != newPass){
            binding.newPasswordLayout.helperText = ""
            binding.cfNewPasswordLayout.helperText = "Confirm password is incorrect"
            binding.newPassword.setBackgroundResource(R.drawable.input_text)
            binding.cfNewPassword.setBackgroundResource(R.drawable.input_error)
            callback(false)
            return
        }

        user?.updatePassword(newPass)?.addOnCompleteListener {task ->
            if (task.isSuccessful){
                callback(true)
                updatePassToDatabase(user.uid, newPass)
                Toast.makeText(requireContext(), "Password changed sucessfully", Toast.LENGTH_SHORT).show()
            }
            else{
                callback(false)
                binding.curPasswordLayout.helperText = "Password is incorrect"
                binding.newPasswordLayout.helperText = ""
                binding.cfNewPasswordLayout.helperText = ""
                binding.curPassword.setBackgroundResource(R.drawable.input_text)
                binding.newPassword.setBackgroundResource(R.drawable.input_text)
                binding.cfNewPassword.setBackgroundResource(R.drawable.input_error)
            }
        }
    }

    private fun updatePassToDatabase(userId: String, newPass: String){
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("user").child(userId)
        userRef.child("password").setValue(newPass)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}