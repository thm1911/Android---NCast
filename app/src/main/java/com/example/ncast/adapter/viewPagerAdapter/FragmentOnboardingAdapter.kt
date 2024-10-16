package com.example.ncast.adapter.viewPagerAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ncast.ui.onBoarding.Onboarding1Fragment
import com.example.ncast.ui.onBoarding.Onboarding2Fragment
import com.example.ncast.ui.onBoarding.Onboarding3Fragment

class FragmentOnboardingAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Onboarding1Fragment()
            1 -> Onboarding2Fragment()
            else -> Onboarding3Fragment()
        }
    }
}