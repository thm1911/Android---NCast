package com.example.ncast.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "User",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    val userId: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = ""
)