package com.example.ncast.utils

import android.app.Application
import android.content.Context

class SharePref {
    companion object {
        fun getIsUserLogIn(application: Application): Boolean {
            val sharePref = application.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
            return sharePref.getBoolean("Log In", false)
        }

        fun setUserLoginState(application: Application, isLogIn: Boolean) {
            val sharePref = application.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
            with(sharePref.edit()) {
                putBoolean("Log In", isLogIn)
                apply()
            }
        }

        fun setImageUrl(application: Application, imageUrl: String) {
            val sharePref = application.getSharedPreferences("image_url", Context.MODE_PRIVATE)
            with(sharePref.edit()) {
                putString("image_url", imageUrl)
                apply()
            }
        }

        fun getImageUrl(application: Application): String? {
            val sharePref = application.getSharedPreferences("image_url", Context.MODE_PRIVATE)
            return sharePref.getString("image_url", null)
        }

        fun setLyric(application: Application, lyric: String) {
            val sharePref = application.getSharedPreferences("lyric", Context.MODE_PRIVATE)
            with(sharePref.edit()) {
                putString("lyric", lyric)
                apply()
            }
        }

        fun getLyric(application: Application): String? {
            val sharePref = application.getSharedPreferences("lyric", Context.MODE_PRIVATE)
            return sharePref.getString("lyric", null)
        }


    }
}