package com.example.android.marsphotos.data.db.entity

import com.google.firebase.database.PropertyName
import java.util.*
import kotlin.collections.ArrayList


data class Food(
    val id: Int,
    val foodName: String,
    val price: Int,
    val foodImg : FoodImg,
    val category: Category,
)

data class FoodBilling(
    @get:PropertyName("foodRequests") @set:PropertyName("foodRequests") var foodRequests: ArrayList<FoodInfo>? = arrayListOf(),
    @get:PropertyName("foodProcessings") @set:PropertyName("foodProcessings") var foodProcessings: ArrayList<FoodInfo>? = arrayListOf(),
    @get:PropertyName("foodDones") @set:PropertyName("foodDones") var foodDones: ArrayList<FoodInfo>? = arrayListOf(),
    @get:PropertyName("foodCanceleds") @set:PropertyName("foodCanceleds") var foodCanceleds: ArrayList<FoodInfo>? = arrayListOf(),
    @get:PropertyName("note") @set:PropertyName("note") var note: String = "",
)

data class FoodInfo(
    @get:PropertyName("foodID") @set:PropertyName("foodID") var foodId: Int = 0,
    @get:PropertyName("billingId") @set:PropertyName("billingId") var billingId: Int = 0,
    @get:PropertyName("quantity") @set:PropertyName("quantity") var quantity: Int = 0,
    @get:PropertyName("note") @set:PropertyName("note") var note: String? = "",
    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt") var updatedAt: Long = Date().time,
    @get:PropertyName("isBring") @set:PropertyName("isBring") var isBring: Boolean? = false,
)

data class FoodItem(
    var food: Food,
    var billingId: Int,
    var note: String?,
    var quantity: Int,
    var updatedAt: Long,
)