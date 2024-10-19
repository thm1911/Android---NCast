package com.example.ncast.ui.mainApp.profile

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ncast.database.user.UserRepository
import com.example.ncast.model.User
import com.example.ncast.utils.SharePref.SharePref

class ProfileViewModel(application: Application): ViewModel() {
    private val userRepository: UserRepository
    val user: LiveData<User>
    init {
        userRepository = UserRepository(application)
        val userId = SharePref.getUserIdFromSharePref(application)
        user = userRepository.getUserById(userId)
    }

    class ProfileViewModelFactory(private val application: Application): ViewModelProvider
    .Factory{
            override fun <T: ViewModel> create (modelClass: Class<T>): T{
                if(modelClass.isAssignableFrom(ProfileViewModel::class.java)){
                    @Suppress("UNCHECKED_CAST")
                    return ProfileViewModel(application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

}