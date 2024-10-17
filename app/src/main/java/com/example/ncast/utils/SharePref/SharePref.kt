package com.example.ncast.utils.SharePref

import android.app.Application
import android.content.Context
import androidx.transition.Visibility.Mode

class SharePref {
    companion object{
        fun getUserIdFromSharePref(application: Application): Long{
            val sharePref = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            return sharePref.getLong("user_id", -1L)
        }

        fun setUserIdToSharePref(application: Application, userId: Long){
            val sharePref = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            with(sharePref.edit()){
                putLong("user_id", userId)
                apply()
            }
        }


    }
}