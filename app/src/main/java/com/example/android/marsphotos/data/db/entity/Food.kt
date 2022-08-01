package com.example.android.marsphotos.data.db.entity

import com.google.firebase.database.PropertyName
import java.util.*

data class Food(
    val id: Int,
    val foodName: String,
    val price: Int,
    val foodImg : FoodImg,
    val category: Category,
)

data class FoodInfo(
    @get:PropertyName("foodID") @set:PropertyName("foodID") var foodId: Int = 0,
    @get:PropertyName("billingId") @set:PropertyName("billingId") var billingId: Int = 0,
    @get:PropertyName("tableName") @set:PropertyName("tableName") var tableName: String = "",
    @get:PropertyName("quantity") @set:PropertyName("quantity") var quantity: Int = 0,
    @get:PropertyName("singlePrice") @set:PropertyName("singlePrice") var singlePrice: Int = 0,
    @get:PropertyName("note") @set:PropertyName("note") var note: String? = "",
    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt") var updatedAt: Long = Date().time,
    @get:PropertyName("isBring") @set:PropertyName("isBring") var isBring: Boolean? = false,
)

data class FoodItem(
    var food: Food,
    var billingId: Int,
    var tableName: String,
    var note: String?,
    var singlePrice: Int,
    var quantity: Int,
    var updatedAt: Long,
    var isBring: Boolean?
)