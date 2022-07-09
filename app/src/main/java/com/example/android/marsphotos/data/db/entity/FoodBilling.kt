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

data class DishBilling(
    @get:PropertyName("dishRequests") @set:PropertyName("dishRequests") var dishRequests: ArrayList<DishInfo>? = arrayListOf(),
    @get:PropertyName("dishProcessings") @set:PropertyName("dishProcessings") var dishProcessings: ArrayList<DishInfo>? = arrayListOf(),
    @get:PropertyName("dishDones") @set:PropertyName("dishDones") var dishDones: ArrayList<DishInfo>? = arrayListOf(),
    @get:PropertyName("dishCanceleds") @set:PropertyName("dishCanceleds") var dishCanceleds: ArrayList<DishInfo>? = arrayListOf(),
    @get:PropertyName("note") @set:PropertyName("note") var note: String = "",
)

data class DishInfo(
    @get:PropertyName("dishID") @set:PropertyName("dishID") var dishId: Int = 0,
    @get:PropertyName("billingId") @set:PropertyName("billingId") var billingId: Int = 0,
    @get:PropertyName("quantity") @set:PropertyName("quantity") var quantity: Int = 0,
    @get:PropertyName("note") @set:PropertyName("note") var note: String? = "",
    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt") var updatedAt: Long = Date().time,
    @get:PropertyName("isBring") @set:PropertyName("isBring") var isBring: Boolean? = false,
)

data class DishItem(
    var dish: Food,
    var billingId: Int,
    var note: String?,
    var quantity: Int,
    var updatedAt: Long,
)