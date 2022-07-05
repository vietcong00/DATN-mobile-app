package com.example.android.marsphotos.ui.dishChef

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsphotos.data.constant.TYPE_DISH_LIST
import com.example.android.marsphotos.databinding.ListItemDishBinding
import com.example.android.marsphotos.databinding.ListItemDishChefBinding
import com.example.android.marsphotos.pojo.DishItem

class DishChefListAdapter internal constructor(
    private val viewModel: DishChefViewModel,
    private val itemDishActionListener: ItemDishActionListener,
    ) :
    ListAdapter<DishItem, DishChefListAdapter.ViewHolder>(UserInfoDiffCallback()) {

    class ViewHolder(private val binding: ListItemDishChefBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            viewModel: DishChefViewModel,
            item: DishItem,
            position: Int,
            itemActionListener: ItemDishActionListener,
        ) {
            binding.viewmodel = viewModel
            binding.dish = item
            binding.index = position
            binding.itemDishActionListener = itemActionListener
            if(viewModel.dishListType == TYPE_DISH_LIST.dishDones) {
                binding.rightSwipe.visibility = View.VISIBLE
            } else{
                binding.rightSwipe.visibility = View.GONE
            }
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position), position, itemDishActionListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemDishChefBinding.inflate(layoutInflater, parent, false)
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

class ItemDishActionListener(val clickListener: (index : Int) -> Unit) {
    fun clickAction(index : Int) = clickListener(index)
}