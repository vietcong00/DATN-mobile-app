package com.example.android.marsphotos.ui.billing

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsphotos.data.db.entity.FoodItem
import com.example.android.marsphotos.util.convertMoney

@BindingAdapter("bind_billing_list")
fun bindBillingList(listView: RecyclerView, items: List<FoodItem>?) {
    items?.let { (listView.adapter as BillingListAdapter).submitList(items) }
}

@BindingAdapter("food_billing_name")
fun bindFoodBillingName(textView: TextView, name: String?) {
    textView.text = name
}

@BindingAdapter("food_single_price")
fun bindFoodBillingSinglePrice(textView: TextView, price: Int?) {
    textView.text = convertMoney(price)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("food_quantity")
fun bindFoodQuantity(textView: TextView, quantity: Int?) {
    textView.text = "x$quantity"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("food_total_price")
fun bindFoodTotalPrice(textView: TextView, foodItem: FoodItem) {
    var totalPrice = 0;
    if (foodItem.quantity != null && foodItem.food.price !=null) {
        totalPrice = foodItem.quantity * foodItem.food.price
    }
    textView.text = convertMoney(totalPrice)
}

