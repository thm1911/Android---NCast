package com.example.ncast.ui.mainApp.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ncast.R
import com.example.ncast.SpacingItem
import com.example.ncast.adapter.recycleViewAdapterSearch.DiscoveriesAdapter
import com.example.ncast.adapter.recycleViewAdapterSearch.UpdateEveryHourAdapter
import com.example.ncast.databinding.FragmentSearchBinding

class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Update Search Songs
        binding.recyclerUpdateEveryHour.layoutManager=GridLayoutManager(requireContext(),2)
        val adapterUpdate=UpdateEveryHourAdapter()
        binding.recyclerUpdateEveryHour.adapter=adapterUpdate

        //Discovery
        binding.recyclerDiscoveries.layoutManager=GridLayoutManager(requireContext(),2)
        val adapterDiscovery=DiscoveriesAdapter()
        binding.recyclerDiscoveries.adapter=adapterDiscovery

        //Adding spacing
        val space=resources.getDimensionPixelSize(R.dimen.space)
        binding.recyclerUpdateEveryHour.addItemDecoration(SpacingItem(space))

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
