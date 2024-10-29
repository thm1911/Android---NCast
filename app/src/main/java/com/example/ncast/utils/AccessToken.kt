package com.example.ncast.utils

import android.util.Base64
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class AccessToken {
    companion object{
        fun getAccessToken(callback: (String?) -> Unit){
            val client = OkHttpClient()
            val clientId = "8d536933e91a49ae80f6ba604a9ba5ce"
            val clientSecret = "0ca56efe9267419ea73c76e7844a4a62"

            val credentials = "$clientId:$clientSecret"
            val encodedCredentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
            val url = "https://accounts.spotify.com/api/token"

            val requestBody = FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build()

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", "Basic $encodedCredentials")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody)
                        val accessToken = jsonResponse.getString("access_token")
                        callback(accessToken)
                    }
                    else {
                        callback(null)
                    }
                }
            })
        }
    }
}