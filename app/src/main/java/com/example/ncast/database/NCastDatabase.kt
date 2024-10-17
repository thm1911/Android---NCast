package com.example.ncast.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ncast.database.user.UserDAO
import com.example.ncast.model.User

@Database(entities = [User::class], version = 1)
abstract class NCastDatabase: RoomDatabase() {
    abstract fun userDAO(): UserDAO
    companion object{
        @Volatile
        private var INSTANCE: NCastDatabase? = null
        fun getInstance(context: Context): NCastDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NCastDatabase::class.java,
                    "NCast_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}