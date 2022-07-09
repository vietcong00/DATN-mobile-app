package com.example.android.marsphotos.ui.dishChef

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.db.entity.DishItem

@BindingAdapter("bind_dish_chef_list")
fun bindDishChefList(listView: RecyclerView, items: List<DishItem>?) {
    items?.let { (listView.adapter as DishChefListAdapter).submitList(items) }
}

@BindingAdapter("dish_chef_img")
fun bindDishChefImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        imgView.load(imgUri) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }
}

@BindingAdapter("dish_chef_name")
fun bindDishChefName(textView: TextView, name: String?) {
    textView.text = name
}

@BindingAdapter("dish_chef_note")
fun bindDishChefNote(textView: TextView, name: String?) {
    textView.text = name
}

@SuppressLint("SetTextI18n")
@BindingAdapter("dish_chef_quantity")
fun bindDishChefQuantity(textView: TextView, quantity: Int?) {
    textView.text = "x$quantity"
}

