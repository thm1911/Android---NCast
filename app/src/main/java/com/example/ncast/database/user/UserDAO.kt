package com.example.ncast.database.user

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.ncast.model.User

@Dao
interface UserDAO {
    @Insert
    suspend fun insertUser(user: User): Long

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE email = :email)")
    suspend fun isEmailExist(email: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE username = :username)")
    suspend fun isUsernameExist(username: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE username = :username AND password = :password)")
    suspend fun checkUserSignIn(username: String, password: String): Boolean

    @Query("SELECT id FROM User WHERE username = :username")
    suspend fun getUserIdFromUsername(username: String): Long

    @Query("SELECT * FROM User WHERE id = :id")
    fun getUserById(id: Long): LiveData<User>

    @Query("UPDATE User SET password = :password WHERE id = :id")
    suspend fun updatePassword(id: Long, password: String)
}