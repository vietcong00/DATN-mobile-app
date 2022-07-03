package com.example.android.marsphotos.ui.dish.processing

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsphotos.pojo.DishProcessingItem

@BindingAdapter("bind_dish_processing_list")
fun bindDishProcessingList(listView: RecyclerView, items: List<DishProcessingItem>?) {
    items?.let { (listView.adapter as DishProcessingListAdapter).submitList(items) }
}

@BindingAdapter("dish_processing_quantity")
fun bindDishProcessingQuantity(textView: TextView, quantity: Int?) {
    textView.text = quantity.toString();
}