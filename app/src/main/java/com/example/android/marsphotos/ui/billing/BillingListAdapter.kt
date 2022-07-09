package com.example.android.marsphotos.ui.billing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsphotos.data.db.entity.FoodItem
import com.example.android.marsphotos.databinding.ListItemBillingBinding

class BillingListAdapter internal constructor(
    private val viewModel: BillingViewModel,
) :
    ListAdapter<FoodItem, BillingListAdapter.ViewHolder>(UserInfoDiffCallback()) {

    class ViewHolder(private val binding: ListItemBillingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            viewModel: BillingViewModel,
            item: FoodItem,
            position: Int,
        ) {
            binding.viewmodel = viewModel
            binding.foodItem = item
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemBillingBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }
}

class UserInfoDiffCallback : DiffUtil.ItemCallback<FoodItem>() {
    override fun areItemsTheSame(
        oldItem: FoodItem,
        newItem: FoodItem
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: FoodItem,
        newItem: FoodItem
    ): Boolean {
        return oldItem.food == newItem.food && oldItem.quantity == newItem.quantity
    }
}