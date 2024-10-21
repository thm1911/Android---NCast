package com.example.ncast.ui.mainApp.profile.changePassword

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ncast.database.user.UserRepository
import com.example.ncast.model.User
import com.example.ncast.utils.SharePref.SharePref
import kotlinx.coroutines.launch

class ChangePassWordViewModel(application: Application): ViewModel() {
    private val userRepository: UserRepository
    val user: LiveData<User>
    init {
        userRepository = UserRepository(application)
        val id = SharePref.getUserIdFromSharePref(application)
        user = userRepository.getUserById(id)
    }

    fun updatePassword(id: Long, password: String) = viewModelScope.launch {
        userRepository.updatePassword(id, password)
    }

    class ChangePassWordViewModelFactory(private val application: Application): ViewModelProvider
    .Factory{
            override fun <T: ViewModel> create (modelClass: Class<T>): T{
                if(modelClass.isAssignableFrom(ChangePassWordViewModel::class.java)){
                    @Suppress("UNCHECKED_CAST")
                    return ChangePassWordViewModel(application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
}