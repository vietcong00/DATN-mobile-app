package com.example.android.marsphotos.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsphotos.data.db.entity.Food
import com.example.android.marsphotos.databinding.GridViewItemBinding

class ProductGridAdapter(
    private val overviewFragment: OverviewFragment
) : ListAdapter<Food,
        ProductGridAdapter.MarsPhotoViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            TODO("Not yet implemented")
            return (oldItem.id == newItem.id)
        }

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            TODO("Not yet implemented")
            return (oldItem.foodName == newItem.foodName)
        }
    }

    class MarsPhotoViewHolder(private var binding: GridViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Food) {
            binding.food = product
            binding.executePendingBindings()
        }
        val view = binding.itemLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarsPhotoViewHolder {
        return MarsPhotoViewHolder(
            GridViewItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: MarsPhotoViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
        holder.view. setOnClickListener {
            overviewFragment.selectProduct(product)
        }
    }
}