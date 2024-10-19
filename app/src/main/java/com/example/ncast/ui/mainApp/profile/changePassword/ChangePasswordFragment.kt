package com.example.ncast.ui.mainApp.profile.changePassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.ncast.R
import com.example.ncast.databinding.FragmentChangePasswordBinding
import com.example.ncast.utils.SharePref.SharePref

class ChangePasswordFragment : Fragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChangePassWordViewModel by viewModels {
        ChangePassWordViewModel.ChangePassWordViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var curPass = ""
        viewModel.user.observe(viewLifecycleOwner){user ->
            curPass = user.password
        }

        binding.save.setOnClickListener {
            check(curPass){check ->
                if(check){
                    viewModel.updatePassword(SharePref.getUserIdFromSharePref(requireActivity().application), binding.cfNewPassword.text.toString())
                    findNavController().popBackStack()
                }
            }
        }

        binding.cancel.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun check(pass: String, callback: (Boolean) -> Unit){
        val curPass = binding.curPassword.text.toString()
        val newPass = binding.newPassword.text.toString()
        val cfNewPass = binding.cfNewPassword.text.toString()

        if(curPass != pass){
            binding.curPasswordLayout.helperText = "Password is incorrect"
            binding.curPassword.setBackgroundResource(R.drawable.input_error)
            callback(false)
            return
        }
        else if(newPass.isNullOrEmpty()){
            binding.curPasswordLayout.helperText = ""
            binding.newPasswordLayout.helperText = "Cannot be left blank"
            binding.curPassword.setBackgroundResource(R.drawable.input_text)
            binding.newPassword.setBackgroundResource(R.drawable.input_error)
            callback(false)
            return
        }
        else if(cfNewPass.isNullOrEmpty()){
            binding.newPasswordLayout.helperText = ""
            binding.cfNewPasswordLayout.helperText = "Cannot be left blank"
            binding.newPassword.setBackgroundResource(R.drawable.input_text)
            binding.cfNewPassword.setBackgroundResource(R.drawable.input_error)
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
        else{
            callback(true)
            return
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}