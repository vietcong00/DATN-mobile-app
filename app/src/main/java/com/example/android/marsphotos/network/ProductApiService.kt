package com.example.android.marsphotos.network

import com.example.android.marsphotos.pojo.*
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


private val gson = Gson()

//Add the following constant for the base URL for the web service.
private const val BASE_URL =
    "https://a2a0-1-55-211-129.ap.ngrok.io/api/v1/common/"

val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create(gson))
    .baseUrl(BASE_URL)
    .build()

interface ProductApiService {
    @GET("food")
    suspend fun getProduct(): Response<IGetListResponse<Food>>

    @PATCH("food-billing/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body updateProductRequest: UpdateProductRequest
    ): BasicResponse

    @POST("food-billing")
    suspend fun createProduct(@Body createProductRequest: CreateProductRequest): BasicResponse

    @DELETE("product/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Int,
    ): BasicResponse

    @GET("food-billing")
    suspend fun getFoodBillingList(
        @Query("tableId") tableId: Int,
    ): Response<IGetListResponse<FoodBilling>>

    @GET("billing/table/{id}")
    suspend fun getBilling(@Path("id") id: Int): Response<Billing>
}