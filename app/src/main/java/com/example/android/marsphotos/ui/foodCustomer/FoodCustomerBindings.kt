package com.example.android.marsphotos.ui.foodCustomer

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.db.entity.FoodItem
import com.example.android.marsphotos.util.convertDateTime

@BindingAdapter("bind_food_list")
fun bindFoodList(listView: RecyclerView, items: List<FoodItem>?) {
    items?.let { (listView.adapter as FoodListAdapter).submitList(items) }
}

@BindingAdapter("food_img")
fun bindFoodImage(imgView: ImageView, imgUrl: String?) {
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

@BindingAdapter("food_name")
fun bindFoodName(textView: TextView, name: String?) {
    textView.text = name
}

@BindingAdapter("food_updated_at")
fun bindFoodUpdatedAt(textView: TextView, timeStamp: Long?) {
    textView.text = convertDateTime(timeStamp)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("food_quantity")
fun bindFoodQuantity(textView: TextView, quantity: Int?) {
    textView.text = "x$quantity"
}

