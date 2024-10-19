package com.example.ncast.ui.mainApp.home
import com.example.ncast.adapter.recyclerViewHomeAdapter.ContinueCategoryAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ncast.R
import com.example.ncast.SpacingItem
import com.example.ncast.adapter.recyclerViewHomeAdapter.RecentMusicListeningAdapter
import com.example.ncast.adapter.recyclerViewHomeAdapter.TopTrendingAdapter

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Continue Listening
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView_continue)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)// cột
        val adapter = ContinueCategoryAdapter()
        recyclerView.adapter = adapter

        //Top Trending
        val recyclerViewTrending: RecyclerView = view.findViewById(R.id.recyclerView_top_trending)
        recyclerViewTrending.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapterTrending = TopTrendingAdapter()
        recyclerViewTrending.adapter = adapterTrending

        // Recent Music Listening
        val recyclerViewRecentMusic: RecyclerView = view.findViewById(R.id.recyclerView_recent_music_listening)
        recyclerViewRecentMusic.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapterRecentMusic = RecentMusicListeningAdapter()
        recyclerViewRecentMusic.adapter = adapterRecentMusic
        val space = resources.getDimensionPixelSize(R.dimen.space)
        recyclerView.addItemDecoration(SpacingItem(space))

        return view
    }
}
