package com.example.android.marsphotos.ui.billing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsphotos.data.db.entity.DishItem
import com.example.android.marsphotos.databinding.ListItemBillingBinding

class BillingListAdapter internal constructor(
    private val viewModel: BillingViewModel,
) :
    ListAdapter<DishItem, BillingListAdapter.ViewHolder>(UserInfoDiffCallback()) {

    class ViewHolder(private val binding: ListItemBillingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            viewModel: BillingViewModel,
            item: DishItem,
            position: Int,
        ) {
            binding.viewmodel = viewModel
            binding.dishItem = item
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