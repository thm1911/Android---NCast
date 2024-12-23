package com.example.ncast.ui.mainApp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ncast.R
import com.example.ncast.databinding.FragmentMainAppBinding
import com.example.ncast.utils.SharePref

class MainAppFragment : Fragment() {
    private var _binding: FragmentMainAppBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainAppBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lấy NavHostFragment từ layout
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        childFragmentManager.beginTransaction()
            .replace(R.id.miniPlayer, MiniPlayerFragment())
            .commit()

        sharedViewModel.isMiniPlayerVisible.observe(viewLifecycleOwner) { isVisible ->
            binding.miniPlayer.visibility = if (isVisible) View.VISIBLE else View.GONE
        }

        binding.miniPlayer.setOnClickListener {
            var idTrack: String = ""
            sharedViewModel.trackId.observe(viewLifecycleOwner){id ->
                idTrack = id
            }
            val bundle = Bundle().apply {
                putString("idTrack", idTrack)
            }
            navController.navigate(R.id.playSongFragment, bundle)
            sharedViewModel.hideMiniPlayer()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
