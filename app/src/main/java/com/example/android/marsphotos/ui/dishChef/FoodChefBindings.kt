package com.example.android.marsphotos.ui.foodChef

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.db.entity.FoodItem
import com.example.android.marsphotos.ui.dishChef.FoodChefListAdapter

@BindingAdapter("bind_food_chef_list")
fun bindFoodChefList(listView: RecyclerView, items: List<FoodItem>?) {
    items?.let { (listView.adapter as FoodChefListAdapter).submitList(items) }
}

@BindingAdapter("food_chef_img")
fun bindFoodChefImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        imgView.load(imgUri) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }
}

@BindingAdapter("food_chef_name")
fun bindFoodChefName(textView: TextView, name: String?) {
    textView.text = name
}

@BindingAdapter("food_chef_note")
fun bindFoodChefNote(textView: TextView, name: String?) {
    textView.text = name
}

@SuppressLint("SetTextI18n")
@BindingAdapter("food_chef_quantity")
fun bindFoodChefQuantity(textView: TextView, quantity: Int?) {
    textView.text = "x$quantity"
}

