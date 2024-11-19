package com.example.ncast.adapter.viewPagerAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ncast.ui.mainApp.playSong.ImageTrackFragment
import com.example.ncast.ui.mainApp.playSong.LyricTrackFragment

class TrackAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ImageTrackFragment()
            else -> LyricTrackFragment()
        }
    }

}