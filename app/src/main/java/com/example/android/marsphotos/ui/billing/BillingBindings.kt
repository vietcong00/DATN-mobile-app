package com.example.android.marsphotos.ui.billing

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.android.marsphotos.R
import com.example.android.marsphotos.pojo.DishItem
import com.example.android.marsphotos.util.convertMoney

@BindingAdapter("bind_billing_list")
fun bindBillingList(listView: RecyclerView, items: List<DishItem>?) {
    items?.let { (listView.adapter as BillingListAdapter).submitList(items) }
}

@BindingAdapter("dish_billing_name")
fun bindDishBillingName(textView: TextView, name: String?) {
    textView.text = name
}

@BindingAdapter("dish_single_price")
fun bindDishBillingSinglePrice(textView: TextView, price: Int?) {
    textView.text = convertMoney(price)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("dish_quantity")
fun bindDishQuantity(textView: TextView, quantity: Int?) {
    textView.text = "x$quantity"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("dish_total_price")
fun bindDishTotalPrice(textView: TextView, dishItem: DishItem) {
    var totalPrice = 0;
    if (dishItem.quantity != null && dishItem.dish.price !=null) {
        totalPrice = dishItem.quantity * dishItem.dish.price
    }
    textView.text = convertMoney(totalPrice)
}

