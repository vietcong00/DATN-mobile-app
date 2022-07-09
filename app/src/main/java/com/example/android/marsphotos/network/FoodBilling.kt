package com.example.android.marsphotos.network

import com.example.android.marsphotos.data.db.entity.DishInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PrepareToPayRequest(
    @SerializedName("dishList") @Expose val dishList: MutableList<DishInfo>? = mutableListOf(),
)
