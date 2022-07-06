package com.example.android.marsphotos.ui.dish

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.android.marsphotos.R
import com.example.android.marsphotos.pojo.DishItem
import com.example.android.marsphotos.util.convertDateTime
import com.example.android.marsphotos.util.convertMoney

@BindingAdapter("bind_dish_list")
fun bindDishList(listView: RecyclerView, items: List<DishItem>?) {
    items?.let { (listView.adapter as DishListAdapter).submitList(items) }
}

@BindingAdapter("dish_img")
fun bindDishImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        imgView.load(imgUri) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }
}

@BindingAdapter("dish_name")
fun bindDishName(textView: TextView, name: String?) {
    textView.text = name
}

@BindingAdapter("dish_updated_at")
fun bindDishUpdatedAt(textView: TextView, timeStamp: Long?) {
    textView.text = convertDateTime(timeStamp)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("dish_quantity")
fun bindDishQuantity(textView: TextView, quantity: Int?) {
    textView.text = "x$quantity"
}

