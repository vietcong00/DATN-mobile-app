package com.example.android.marsphotos.ui.foodWaiter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsphotos.data.constant.TYPE_DISH_LIST
import com.example.android.marsphotos.data.db.entity.FoodItem
import com.example.android.marsphotos.databinding.ListItemFoodWaiterBinding

class FoodWaiterListAdapter internal constructor(
    private val viewModel: FoodWaiterViewModel,
    private val itemFoodBringListener: ItemFoodBringListener,
    ) :
    ListAdapter<FoodItem, FoodWaiterListAdapter.ViewHolder>(UserInfoDiffCallback()) {

    class ViewHolder(private val binding: ListItemFoodWaiterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            viewModel: FoodWaiterViewModel,
            item: FoodItem,
            position: Int,
            itemBringListener: ItemFoodBringListener,
        ) {
            binding.viewmodel = viewModel
            binding.foodItem = item
            binding.index = position
            binding.itemFoodBringListener = itemBringListener
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position), position, itemFoodBringListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemFoodWaiterBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }
}

class UserInfoDiffCallback : DiffUtil.ItemCallback<FoodItem>() {
    override fun areItemsTheSame(
        oldItem: FoodItem,
        newItem: FoodItem
    ): Boolean {
        return oldItem.food.id === newItem.food.id && oldItem.billingId === newItem.billingId && oldItem.updatedAt === oldItem.updatedAt
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: FoodItem,
        newItem: FoodItem
    ): Boolean {
        return oldItem.isBring === newItem.isBring
    }
}

class ItemFoodBringListener(val clickListener: (food : FoodItem) -> Unit) {
    fun clickBring(food : FoodItem) = clickListener(food)
}