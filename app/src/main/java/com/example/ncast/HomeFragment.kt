package com.example.ncast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    true
                }
                R.id.bottom_search -> {
                    findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
                    true
                }
                R.id.bottom_settings -> {
                    findNavController().navigate(R.id.action_homeFragment_to_libraryFragment)
                    true
                }
                R.id.bottom_profile -> {
                    findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                    true
                }
                else -> false
            }
        }

        return view
    }
}
