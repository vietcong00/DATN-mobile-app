package com.example.android.marsphotos.ui.foodCustomer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsphotos.data.constant.TYPE_DISH_LIST
import com.example.android.marsphotos.data.db.entity.DishItem
import com.example.android.marsphotos.databinding.ListItemFoodCustomerBinding

class DishListAdapter internal constructor(
    private val viewModel: DishViewModel,
    private val itemDishCanceledListener: ItemDishCanceledListener,
) :
    ListAdapter<DishItem, DishListAdapter.ViewHolder>(UserInfoDiffCallback()) {

    class ViewHolder(private val binding: ListItemFoodCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            viewModel: DishViewModel,
            item: DishItem,
            position: Int,
            itemCanceledListener: ItemDishCanceledListener
        ) {
            binding.viewmodel = viewModel
            binding.dishItem = item
            binding.index = position
            binding.itemDishCanceledListener = itemCanceledListener
            if(viewModel.dishListType == TYPE_DISH_LIST.dishRequests) {
                binding.rightSwipe.visibility = View.VISIBLE
            } else{
                binding.rightSwipe.visibility = View.GONE
            }
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position), position, itemDishCanceledListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemFoodCustomerBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }
}

class UserInfoDiffCallback : DiffUtil.ItemCallback<DishItem>() {
    override fun areItemsTheSame(
        oldItem: DishItem,
        newItem: DishItem
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: DishItem,
        newItem: DishItem
    ): Boolean {
        return oldItem.dish == newItem.dish && oldItem.quantity == newItem.quantity
    }
}

class ItemDishCanceledListener(val clickListener: (index : Int) -> Unit) {
    fun canceled(index : Int) = clickListener(index)
}