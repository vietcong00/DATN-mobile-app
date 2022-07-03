package com.example.android.marsphotos.network

import com.example.android.marsphotos.pojo.FoodImg
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CreateProductRequest(
    @SerializedName("foodId") @Expose val foodId: Int,
    @SerializedName("billingId") @Expose val billingId: Int,
    @SerializedName("selectedCount") @Expose val selectedCount: Int,
    @SerializedName("note") @Expose val note: String,
)

data class UpdateProductRequest(
    @SerializedName("foodId") @Expose val foodId: Int,
    @SerializedName("selectedCount") @Expose val selectedCount: Int,
    @SerializedName("canceledCount") @Expose val canceledCount: Int,
    @SerializedName("note") @Expose val note: String,
)
