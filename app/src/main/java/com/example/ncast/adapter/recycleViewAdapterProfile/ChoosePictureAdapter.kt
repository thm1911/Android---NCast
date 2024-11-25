package com.example.ncast.adapter.recycleViewAdapterProfile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ncast.databinding.ItemChooseAvtProfileBinding

class ChoosePictureAdapter(
    private val imageUrls: List<String>,
    private val onImageSelected: (String) -> Unit
) : RecyclerView.Adapter<ChoosePictureAdapter.ChoosePictureHolder>() {

    inner class ChoosePictureHolder(val binding: ItemChooseAvtProfileBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChoosePictureHolder {
        val binding =
            ItemChooseAvtProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChoosePictureHolder(binding)
    }

    override fun getItemCount(): Int = imageUrls.size

    override fun onBindViewHolder(holder: ChoosePictureHolder, position: Int) {
        val imageUrl = imageUrls[position]
        Glide.with(holder.binding.root.context)
            .load(imageUrl)
            .into(holder.binding.itemImage)

        holder.binding.itemImage.setOnClickListener {
            onImageSelected(imageUrl)
        }
    }
}

