package com.example.android.marsphotos.ui.foodChef

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsphotos.data.constant.TYPE_DISH_LIST
import com.example.android.marsphotos.data.db.entity.FoodItem
import com.example.android.marsphotos.databinding.ListItemFoodChefBinding

class FoodChefListAdapter internal constructor(
    private val viewModel: FoodChefViewModel,
    private val itemFoodActionListener: ItemFoodActionListener,
) :
    ListAdapter<FoodItem, FoodChefListAdapter.ViewHolder>(UserInfoDiffCallback()) {

    class ViewHolder(private val binding: ListItemFoodChefBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            viewModel: FoodChefViewModel,
            item: FoodItem,
            position: Int,
            itemActionListener: ItemFoodActionListener,
        ) {
            binding.viewmodel = viewModel
            binding.foodItem = item
            binding.index = position
            binding.itemFoodActionListener = itemActionListener
            if (viewModel.foodListType !== TYPE_DISH_LIST.foodDones) {
                binding.rightSwipe.visibility = View.VISIBLE
            } else {
                binding.rightSwipe.visibility = View.GONE
            }
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position), position, itemFoodActionListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemFoodChefBinding.inflate(layoutInflater, parent, false)
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
        return oldItem.note === newItem.note
    }
}

class ItemFoodActionListener(val clickListener: (food: FoodItem) -> Unit) {
    fun clickAction(food: FoodItem) = clickListener(food)
}