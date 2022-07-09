package com.example.android.marsphotos.data.db.entity

import com.google.firebase.database.PropertyName
import kotlin.collections.ArrayList

data class FoodBilling(
    @get:PropertyName("foodRequests") @set:PropertyName("foodRequests") var foodRequests: ArrayList<FoodInfo>? = arrayListOf(),
    @get:PropertyName("foodProcessings") @set:PropertyName("foodProcessings") var foodProcessings: ArrayList<FoodInfo>? = arrayListOf(),
    @get:PropertyName("foodDones") @set:PropertyName("foodDones") var foodDones: ArrayList<FoodInfo>? = arrayListOf(),
    @get:PropertyName("foodCanceleds") @set:PropertyName("foodCanceleds") var foodCanceleds: ArrayList<FoodInfo>? = arrayListOf(),
    @get:PropertyName("note") @set:PropertyName("note") var note: String = "",
)