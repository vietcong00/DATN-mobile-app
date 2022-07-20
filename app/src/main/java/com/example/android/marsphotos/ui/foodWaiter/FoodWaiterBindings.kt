package com.example.android.marsphotos.ui.foodWaiter

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.db.entity.FoodItem

@BindingAdapter("bind_food_waiter_list")
fun bindFoodWaiterList(listView: RecyclerView, items: List<FoodItem>?) {
    items?.let { (listView.adapter as FoodWaiterListAdapter).submitList(items) }
}

@BindingAdapter("food_waiter_img")
fun bindFoodWaiterImage(imgView: ImageView, imgUrl: String?) {
    if (imgUrl.isNullOrEmpty() || imgUrl.isNullOrBlank()) {
        imgView.setImageResource(R.drawable.ic_broken_image)
    } else {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        imgView.load(imgUri) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }
}

@BindingAdapter("food_waiter_name")
fun bindFoodWaiterName(textView: TextView, name: String?) {
    textView.text = name
}

@BindingAdapter("food_waiter_table")
fun bindFoodWaiterTable(textView: TextView, tableName: String?) {
    textView.text = tableName
}

@BindingAdapter("food_waiter_note")
fun bindFoodWaiterNote(textView: TextView, name: String?) {
    textView.text = name
}

@SuppressLint("SetTextI18n")
@BindingAdapter("food_waiter_quantity")
fun bindFoodWaiterQuantity(textView: TextView, quantity: Int?) {
    textView.text = "x$quantity"
}

