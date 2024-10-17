package com.example.ncast.ui.signUp.user

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ncast.database.user.UserRepository
import com.example.ncast.model.User
import kotlinx.coroutines.launch

class UserSignUpViewModel(application: Application): ViewModel() {
    private val userRepository: UserRepository
    init {
        userRepository = UserRepository(application)
    }

    fun insertUser(user: User, callback: (Long) -> Unit) = viewModelScope.launch {
        val id = userRepository.insertUser(user)
        callback(id)
    }

    fun isUsernameExist(username: String, callback: (Boolean) -> Unit) = viewModelScope.launch {
        val exist = userRepository.isUsernameExist(username)
        callback(exist)
    }

    class UserSignUpViewModelFactory(private val application: Application): ViewModelProvider
    .Factory{
            override fun <T: ViewModel> create (modelClass: Class<T>): T{
                if(modelClass.isAssignableFrom(UserSignUpViewModel::class.java)){
                    @Suppress("UNCHECKED_CAST")
                    return UserSignUpViewModel(application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
}