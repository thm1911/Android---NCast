package com.example.ncast.database.user

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.ncast.database.NCastDatabase
import com.example.ncast.model.User

class UserRepository(application: Application) {
    private val userDAO: UserDAO
    init {
        userDAO = NCastDatabase.getInstance(application).userDAO()
    }

    suspend fun insertUser(user: User): Long = userDAO.insertUser(user)
    suspend fun isEmailExist(email: String): Boolean = userDAO.isEmailExist(email)
    suspend fun isUsernameExist(username: String): Boolean = userDAO.isUsernameExist(username)
    suspend fun checkUserSignIn(username: String,password: String): Boolean = userDAO.checkUserSignIn(username, password)
    suspend fun getUserIdFromUsername(username: String): Long = userDAO.getUserIdFromUsername(username)
    fun getUserById(id: Long): LiveData<User> = userDAO.getUserById(id)
    suspend fun updatePassword(id: Long, password: String) = userDAO.updatePassword(id, password)
}