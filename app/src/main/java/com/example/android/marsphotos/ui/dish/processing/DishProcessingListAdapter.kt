package com.example.android.marsphotos.ui.dish.processing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsphotos.databinding.ListItemDishProcessingBinding
import com.example.android.marsphotos.pojo.DishProcessingItem

class DishProcessingListAdapter internal constructor(private val viewModel: DishProcessingViewModel) :
    ListAdapter<DishProcessingItem, DishProcessingListAdapter.ViewHolder>(UserInfoDiffCallback()) {

    class ViewHolder(private val binding: ListItemDishProcessingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: DishProcessingViewModel, item: DishProcessingItem) {
            binding.viewmodel = viewModel
            binding.dish = item
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemDishProcessingBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }
}

class UserInfoDiffCallback : DiffUtil.ItemCallback<DishProcessingItem>() {
    override fun areItemsTheSame(oldItem: DishProcessingItem, newItem: DishProcessingItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: DishProcessingItem, newItem: DishProcessingItem): Boolean {
        return oldItem.dish == newItem.dish && oldItem.quantity == newItem.quantity
    }
}