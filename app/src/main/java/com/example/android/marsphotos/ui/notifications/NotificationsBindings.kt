package com.example.android.marsphotos.ui.notifications

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsphotos.data.db.entity.UserNotification

@BindingAdapter("bind_notifications_list")
fun bindNotificationsList(listView: RecyclerView, items: List<UserNotification>?) {
    items?.let { (listView.adapter as NotificationsListAdapter).submitList(items) }
}
