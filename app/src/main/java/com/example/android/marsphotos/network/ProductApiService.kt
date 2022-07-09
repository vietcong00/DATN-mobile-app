package com.example.android.marsphotos.network

import com.example.android.marsphotos.data.db.entity.Billing
import com.example.android.marsphotos.data.db.entity.Food
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


private val gson = Gson()

//Add the following constant for the base URL for the web service.
private const val BASE_URL =
    "https://50c4-1-55-211-129.ap.ngrok.io/api/v1/common/"

val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create(gson))
    .baseUrl(BASE_URL)
    .build()

interface ProductApiService {
    @GET("food")
    suspend fun getProduct(): Response<IGetListResponse<Food>>

    @GET("billing/table/{id}")
    suspend fun getBilling(@Path("id") id: Int): Response<Billing>

    @PATCH("billing/{id}")
    suspend fun prepareToPay(
        @Path("id") id: Int,
        @Body prepareToPayRequest: PrepareToPayRequest
    ): BasicResponse
}