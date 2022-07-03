package com.example.android.marsphotos.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsphotos.data.db.entity.UserNotification
import com.example.android.marsphotos.databinding.ListItemNotificationBinding

class NotificationsListAdapter internal constructor(private val viewModel: NotificationsViewModel) :
    ListAdapter<UserNotification, NotificationsListAdapter.ViewHolder>(UserInfoDiffCallback()) {

    class ViewHolder(private val binding: ListItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: NotificationsViewModel, item: UserNotification) {
            binding.viewmodel = viewModel
            binding.userinfo = item
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemNotificationBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }
}

class UserInfoDiffCallback : DiffUtil.ItemCallback<UserNotification>() {
    override fun areItemsTheSame(oldItem: UserNotification, newItem: UserNotification): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: UserNotification, newItem: UserNotification): Boolean {
        return oldItem.userID == newItem.userID
    }
}