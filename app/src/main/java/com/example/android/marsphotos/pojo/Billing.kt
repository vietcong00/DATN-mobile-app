package com.example.android.marsphotos.pojo

import java.util.*

enum class BillingStatus {
    eating,
    wait_for_pay,
    canceled,
    paid,
}

enum class PaymentMethod {
    ready_cash,
    banking,
}

enum class ReasonCanceled {
    out_of_material,
    long_waiting_time,
    change_to_another,
    another_reason,
}

data class Billing(
    val id: Int,
    val customerName: String,
    val customerPhone: String,
    val tableId: Int,
    val cashierId: Int,
    val paymentTotal: Int,
    val paymentMethod: PaymentMethod,
    val paymentTime: Date,
    val arrivalTime: Date,
    val billingStatus: BillingStatus,
    val reasonCanceled: ReasonCanceled,
    val note: String,
)