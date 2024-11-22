package com.example.ncast.ui.onBoarding

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.ncast.R
import com.example.ncast.adapter.viewPagerAdapter.FragmentOnboardingAdapter
import com.example.ncast.databinding.FragmentOnboardingBinding
import com.example.ncast.utils.SharePref
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class OnboardingFragment : Fragment() {
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkLogInState()

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        val adapter = FragmentOnboardingAdapter(parentFragmentManager, lifecycle)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->

        }.attach()


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    binding.Continue.visibility = View.INVISIBLE
                    binding.skip.visibility = View.VISIBLE
                } else if (position == 1) {
                    binding.Continue.visibility = View.INVISIBLE
                    binding.skip.visibility = View.VISIBLE
                } else {
                    binding.Continue.visibility = View.VISIBLE
                    binding.skip.visibility = View.INVISIBLE
                }
            }
        })

        binding.Continue.setOnClickListener {
            findNavController().navigate(OnboardingFragmentDirections.actionOnboardingFragmentToCreateAccountFragment())
        }
        binding.skip.setOnClickListener {
            findNavController().navigate(OnboardingFragmentDirections.actionOnboardingFragmentToCreateAccountFragment())
        }

    }

    private fun checkLogInState() {
        val isLogIn = SharePref.getIsUserLogIn(requireActivity().application)
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.onboardingFragment, true)
            .build()

        if (isLogIn) {
            findNavController().navigate(
                OnboardingFragmentDirections.actionOnboardingFragmentToMainAppFragment(),
                navOptions
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}