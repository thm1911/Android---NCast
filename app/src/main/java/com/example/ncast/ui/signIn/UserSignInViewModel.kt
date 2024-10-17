package com.example.ncast.ui.signIn

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ncast.database.user.UserRepository
import kotlinx.coroutines.launch

class UserSignInViewModel(application: Application): ViewModel() {
    private val userRepository: UserRepository
    init {
        userRepository = UserRepository(application)
    }

    fun checkUserSignIn(username: String, password: String, callback: (Boolean) -> Unit) = viewModelScope.launch {
        val check = userRepository.checkUserSignIn(username, password)
        callback(check)
    }

    fun getUserIdFromUsername(username: String, callback: (Long) -> Unit) = viewModelScope.launch {
        val id = userRepository.getUserIdFromUsername(username)
        callback(id)
    }

    class UserSignInViewModelFactory(private val application: Application): ViewModelProvider
    .Factory{
            override fun <T: ViewModel> create (modelClass: Class<T>): T{
                if(modelClass.isAssignableFrom(UserSignInViewModel::class.java)){
                    @Suppress("UNCHECKED_CAST")
                    return UserSignInViewModel(application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
}