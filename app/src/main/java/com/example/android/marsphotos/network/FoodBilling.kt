package com.example.android.marsphotos.network

import com.example.android.marsphotos.data.db.entity.FoodInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PrepareToPayRequest(
    @SerializedName("foodList") @Expose val foodList: MutableList<FoodInfo>? = mutableListOf(),
)
