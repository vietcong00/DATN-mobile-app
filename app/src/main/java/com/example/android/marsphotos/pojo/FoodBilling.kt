package com.example.android.marsphotos.pojo

data class FoodBilling(
    val id: Int?,
    var foodId: Int?,
    var billingId: Int?,
    val selectedCount: Int,
    val processingCount: Int,
    val doneCount: Int,
    val canceledCount: Int,
    val reasonCanceled: ReasonCanceled?,
    val note: String,
)

data class DishItem(
    var dish: Food,
    var note: String?,
    var quantity: Int,
    var updatedAt: Long,
)