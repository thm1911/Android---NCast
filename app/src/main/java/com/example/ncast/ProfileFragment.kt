package com.example.ncast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_profile

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
                    true
                }
                R.id.bottom_search -> {
                    findNavController().navigate(R.id.action_profileFragment_to_searchFragment)
                    true
                }
                R.id.bottom_settings -> {
                    findNavController().navigate(R.id.action_profileFragment_to_libraryFragment)
                    true
                }
                R.id.bottom_profile -> true
                else -> false
            }
        }

        return view
    }
}
