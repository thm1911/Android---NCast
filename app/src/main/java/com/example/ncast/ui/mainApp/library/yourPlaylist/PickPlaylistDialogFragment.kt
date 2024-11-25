package com.example.ncast.ui.mainApp.library.yourPlaylist

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ncast.R
import com.example.ncast.adapter.recycleViewAdapterLibrary.PickPlaylistAdapter
import com.example.ncast.databinding.DialogCustomConfirmationBinding
import com.example.ncast.databinding.FragmentPickPlaylistDialogBinding
import com.example.ncast.ui.mainApp.library.LibraryViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PickPlaylistDialogFragment : DialogFragment() {
    private var _binding: FragmentPickPlaylistDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PickPlaylistAdapter
    private var idTrack: String = ""
    private val viewModel: LibraryViewModel by viewModels {
        LibraryViewModel.LibraryViewModelFactory()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog ?: return

        val bottomSheet =
            dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(it)
            behavior.state =
                com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        }

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT

        )
        dialog.window?.setGravity(android.view.Gravity.CENTER)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPickPlaylistDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPlaylistContainsTrack(idTrack)
        initRecyclerView()

        binding.save.setOnClickListener {
            val dialogBinding = DialogCustomConfirmationBinding.inflate(LayoutInflater.from(requireContext()))
            val dialog = AlertDialog.Builder(requireContext()).setView(dialogBinding.root).create()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            dialogBinding.dialogTitle.text = "Save these changes"
            dialogBinding.dialogMessage.text = "Are you sure you want to save them?"

            dialogBinding.btnConfirm.setOnClickListener {
                saveData()
                dialog.dismiss()
                dismiss()
            }

            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
                dismiss()
            }

            dialog.show()
        }


    }

    fun setIdTrack(idTrack: String) {
        this.idTrack = idTrack
    }

    private fun initRecyclerView() {
        adapter = PickPlaylistAdapter(mutableListOf()) {

        }
        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            binding.total.setText(String.format("%d playlists", playlist.size))
            adapter.setData(playlist)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun saveData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        adapter.getAllPlaylist().forEach { yourPlaylist ->
            val playlistRef = FirebaseDatabase.getInstance().getReference("user")
                .child(userId!!)
                .child("playlist")
                .child(yourPlaylist.name)
                .child("track")
            playlistRef.get().addOnSuccessListener { datasnapshot ->
                val trackIdList = mutableListOf<String>()
                datasnapshot.children.forEach { snapshot ->
                    snapshot.getValue(String::class.java)?.let {
                        trackIdList.add(it)
                    }
                }
                if(yourPlaylist.ticked){
                    if (!trackIdList.contains(idTrack)) trackIdList.add(idTrack)
                }
                else{
                    if (trackIdList.contains(idTrack)) trackIdList.remove(idTrack)
                }

                playlistRef.setValue(trackIdList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}