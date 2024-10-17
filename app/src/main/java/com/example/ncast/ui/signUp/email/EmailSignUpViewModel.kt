package com.example.ncast.ui.signUp.email

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ncast.database.user.UserRepository
import kotlinx.coroutines.launch

class EmailSignUpViewModel(application: Application): ViewModel() {
    private val userRepository: UserRepository
    init {
        userRepository = UserRepository(application)
    }

    fun isEmailExist(email: String, callback: (Boolean) -> Unit) = viewModelScope.launch{
        val exist = userRepository.isEmailExist(email)
        callback(exist)
    }

    class EmailSignUpViewModelFactory(private val application: Application):
    ViewModelProvider.Factory{
            override fun <T: ViewModel> create (modelClass: Class<T>): T{
                if(modelClass.isAssignableFrom(EmailSignUpViewModel::class.java)){
                    @Suppress("UNCHECKED_CAST")
                    return EmailSignUpViewModel(application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
}