package com.example.ncast.adapter.recycleViewAdapterSearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ncast.R
import com.example.ncast.databinding.ItemKeySearchRecentlyBinding

class KeySearchAdapter(
    private val listKey: MutableList<String>,
    private val onClick: (String) -> Unit,
    private val onCLickDelete: (String) -> Unit
) : RecyclerView.Adapter<KeySearchAdapter.KeySearchViewHolder>() {

    inner class KeySearchViewHolder(val binding: ItemKeySearchRecentlyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(key: String) {
            binding.key.setText(key)
            binding.delete.setOnClickListener {
                onCLickDelete(key)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeySearchViewHolder {
        val binding =
            ItemKeySearchRecentlyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KeySearchViewHolder(binding)
    }

    override fun getItemCount(): Int = listKey.size

    override fun onBindViewHolder(holder: KeySearchViewHolder, position: Int) {
        val key = listKey[position]
        holder.bind(key)
        holder.itemView.setOnClickListener {
            onClick(key)
        }
    }

    fun setData(listKey: List<String>) {
        val result = DiffUtil.calculateDiff(KeySearchDiffUtil(this.listKey, listKey))
        this.listKey.clear()
        this.listKey.addAll(listKey)
        result.dispatchUpdatesTo(this)
    }

    class KeySearchDiffUtil(private val oldList: List<String>, private val newList: List<String>) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }
}