package com.example.ncast.utils

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Track {
    companion object {
        fun getLyricFromFirebase(trackId: String, onResult: (String) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val trackRef = database.getReference("tracks")
            trackRef.child(trackId).child("lyric")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val lyric = snapshot.getValue(String::class.java) ?: ""
                        onResult(lyric)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onResult("")
                    }

                })
        }

        fun getUrlFromDatabase(trackId: String, onResult: (String) -> Unit) {
            val database = FirebaseDatabase.getInstance()
            val trackRef = database.getReference("tracks").child(trackId)
            trackRef.child("trackUrl")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val fullUrl = snapshot.getValue(String::class.java) ?: ""
                        onResult(fullUrl)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onResult("")
                    }
                })
        }
    }
}