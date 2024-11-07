package com.example.ncast.utils

import android.util.Base64
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class AccessToken {
    companion object {
        fun getAccessToken(callback: (String?) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val clientIdRef = database.getReference("Access Token").child("clientId")
            val clientSecretRef = database.getReference("Access Token").child("clientSecret")
            var clientId: String? = null
            var clientSecret: String? = null

            fun requestAccessToken() {
                if (clientId != null && clientSecret != null) {
                    val client = OkHttpClient()
                    val credentials = "$clientId:$clientSecret"
                    val encodedCredentials =
                        Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
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
                            } else {
                                callback(null)
                            }
                        }
                    })
                }
            }
            clientIdRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    clientId = snapshot.getValue(String::class.java) ?: ""
                    requestAccessToken()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

            clientSecretRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    clientSecret = snapshot.getValue(String::class.java) ?: ""
                    requestAccessToken()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}