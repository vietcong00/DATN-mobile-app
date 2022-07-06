package com.example.android.marsphotos.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun convertFileToByteArray(context: Context, uri: Uri): ByteArray {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

    return byteArrayOutputStream.toByteArray()
}

fun convertMoney(price: Int?): String {
    val format: NumberFormat = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 0
    format.currency = Currency.getInstance("VND")
    return if (price != null) {
        format.format(price)
    } else {
        format.format(0)
    }
}

fun convertDateTime(timeStamp: Long?): String? {
    return try {
        val sdf = SimpleDateFormat("HH:mm:ss")
        val netDate = timeStamp?.let { Date(it) }
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}
